// app.js

var useMockData = false;
var api = useMockData ? apimock : apiclient;

var app = (function () {

    var _author = null;
    var _blueprints = []; // lista de {name, points: number}
    var _currentBlueprintName = null; // nombre del blueprint abierto
    var _currentBlueprintPoints = null; // referencia al array de puntos del blueprint abierto

    // --- RENDER tabla de planos ---
    function _renderTable() {
        $("#blueprintsTable tbody").empty();
        _blueprints.map(function (bp) {
            var row = `<tr>
                         <td>${bp.name}</td>
                         <td>${bp.points}</td>
                         <td><button class="btn btn-primary open-blueprint" data-name="${bp.name}">Open</button></td>
                       </tr>`;
            $("#blueprintsTable tbody").append(row);
        });

        var total = _blueprints.reduce(function (acc, bp) {
            return acc + bp.points;
        }, 0);
        $("#totalPoints").text(total);

        $(".open-blueprint").off("click").on("click", function () {
            var blueprintName = $(this).data("name");
            app.openBlueprint(_author, blueprintName);
        });
    }

    // --- DIBUJO ---
    function _drawBlueprint(points) {
        var canvas = document.getElementById("blueprintCanvas");
        if (!canvas) return;
        var ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        if (!points || points.length === 0) return;

        ctx.beginPath();
        ctx.moveTo(points[0].x, points[0].y);
        for (var i = 1; i < points.length; i++) {
            ctx.lineTo(points[i].x, points[i].y);
        }
        ctx.strokeStyle = "#007bff";
        ctx.lineWidth = 2;
        ctx.stroke();
    }

    function _getCanvasRelativeCoordinates(evt) {
        var canvas = document.getElementById("blueprintCanvas");
        var rect = canvas.getBoundingClientRect();
        var clientX = evt.clientX, clientY = evt.clientY;
        if (evt.touches && evt.touches.length > 0) {
            clientX = evt.touches[0].clientX;
            clientY = evt.touches[0].clientY;
        }
        var x = clientX - rect.left;
        var y = clientY - rect.top;
        return { x: Math.round(x), y: Math.round(y) };
    }

    function _onCanvasPointerDown(evt) {
        if (!_currentBlueprintPoints) {
            return;
        }

        evt.preventDefault();

        var pt = _getCanvasRelativeCoordinates(evt);

        _currentBlueprintPoints.push(pt);

        _drawBlueprint(_currentBlueprintPoints);

        _updateCurrentBlueprintCount(_currentBlueprintPoints.length);
    }

    function _updateCurrentBlueprintCount(newCount) {
        for (var i = 0; i < _blueprints.length; i++) {
            if (_blueprints[i].name === _currentBlueprintName) {
                _blueprints[i].points = newCount;
                break;
            }
        }
        _renderTable();
    }

    function _initCanvasHandlers() {
        var canvas = document.getElementById("blueprintCanvas");
        if (!canvas) return;

        if (window.PointerEvent) {
            canvas.addEventListener("pointerdown", _onCanvasPointerDown, false);
        } else {
            canvas.addEventListener("mousedown", _onCanvasPointerDown, false);
            canvas.addEventListener("touchstart", _onCanvasPointerDown, false);
        }

        canvas.addEventListener("touchmove", function (e) { e.preventDefault(); }, false);
    }

    return {
        init: function () {
            _initCanvasHandlers();
        },

        setAuthor: function (authorName) {
            _author = authorName;
            $("#authorName").text(_author + "'s blueprints:");
        },

        getAuthor: function () {
            return _author;
        },

        updateBlueprints: function () {
            if (_author == null) {
                alert("Por favor ingrese un autor.");
                return;
            }

            api.getBlueprintsByAuthor(_author, function (data) {
                if (!data) {
                    alert("No se encontraron planos para este autor.");
                    $("#blueprintsTable tbody").empty();
                    $("#totalPoints").text(0);
                    $("#authorName").text("");
                    return;
                }

                _blueprints = data.map(function (bp) {
                    return { name: bp.name, points: bp.points.length };
                });

                _currentBlueprintName = null;
                _currentBlueprintPoints = null;
                $("#currentBlueprint").text("Current blueprint: ");

                _renderTable();
            });
        },

        openBlueprint: function (author, name) {
            api.getBlueprintsByNameAndAuthor(author, name, function (bp) {
                if (bp) {
                    $("#currentBlueprint").text("Drawing: " + bp.name);
                    _currentBlueprintName = bp.name;
                    _currentBlueprintPoints = bp.points;
                    _drawBlueprint(_currentBlueprintPoints);
                } else {
                    alert("Blueprint no encontrado.");
                }
            });
        },

        saveCurrentBlueprint: function () {
            if (!_currentBlueprintName || !_currentBlueprintPoints) {
                alert("No hay ningún blueprint abierto para guardar.");
                return;
            }

            var updatedBlueprint = {
                author: _author,
                name: _currentBlueprintName,
                points: _currentBlueprintPoints
            };

            var putPromise = new Promise(function (resolve, reject) {
                api.updateBlueprint(_author, _currentBlueprintName, updatedBlueprint, function (ok) {
                    if (ok) resolve();
                    else reject("Error al actualizar el blueprint");
                });
            });

            putPromise
                .then(function () {
                    return new Promise(function (resolve) {
                        api.getBlueprintsByAuthor(_author, function (data) {
                            _blueprints = data.map(function (bp) {
                                return { name: bp.name, points: bp.points.length };
                            });
                            resolve();
                        });
                    });
                })
                .then(function () {
                    _renderTable();
                    alert("Blueprint actualizado correctamente.");
                })
                .catch(function (err) {
                    console.error(err);
                    alert("No se pudo actualizar el blueprint.");
                });
        },

        createNewBlueprint: function () {
            if (!_author) {
                alert("Por favor seleccione un autor antes de crear un blueprint.");
                return;
            }

            var newName = prompt("Ingrese el nombre del nuevo blueprint:");
            if (!newName || newName.trim() === "") {
                alert("Debe ingresar un nombre válido.");
                return;
            }

            var canvas = document.getElementById("blueprintCanvas");
            var ctx = canvas.getContext("2d");
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            _currentBlueprintName = newName;
            _currentBlueprintPoints = [];
            $("#currentBlueprint").text("Creating new blueprint: " + newName);
            $("#save-update").off("click").on("click", function () {
                app.saveNewBlueprint();
            });
        },

        saveNewBlueprint: function () {
            if (!_currentBlueprintName || !_currentBlueprintPoints) {
                alert("No hay ningún blueprint nuevo para guardar.");
                return;
            }

            var newBlueprint = {
                author: _author,
                name: _currentBlueprintName,
                points: _currentBlueprintPoints
            };

            var postPromise = new Promise(function (resolve, reject) {
                api.createBlueprint(_author, newBlueprint, function (ok) {
                    if (ok) resolve();
                    else reject("Error al crear el blueprint");
                });
            });

            postPromise
                .then(function () {
                    return new Promise(function (resolve) {
                        api.getBlueprintsByAuthor(_author, function (data) {
                            _blueprints = data.map(function (bp) {
                                return { name: bp.name, points: bp.points.length };
                            });
                            resolve();
                        });
                    });
                })
                .then(function () {
                    _renderTable();
                    alert("Blueprint creado correctamente.");
                    $("#save-update").off("click").on("click", function () {
                        app.saveCurrentBlueprint();
                    });
                })
                .catch(function (err) {
                    console.error(err);
                    alert("No se pudo crear el blueprint.");
                });
        },

        deleteCurrentBlueprint: function () {
            if (!_currentBlueprintName || !_currentBlueprintPoints) {
                alert("No hay ningún blueprint abierto para eliminar.");
                return;
            }

            if (!confirm("¿Seguro que desea eliminar el blueprint '" + _currentBlueprintName + "'?")) {
                return;
            }

            // Limpiar el canvas
            var canvas = document.getElementById("blueprintCanvas");
            var ctx = canvas.getContext("2d");
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            // Promesa DELETE
            var deletePromise = new Promise(function (resolve, reject) {
                api.deleteBlueprint(_author, _currentBlueprintName, function (ok) {
                    if (ok) resolve();
                    else reject("Error al eliminar el blueprint");
                });
            });

            deletePromise
                .then(function () {
                    return new Promise(function (resolve) {
                        api.getBlueprintsByAuthor(_author, function (data) {
                            _blueprints = data.map(function (bp) {
                                return { name: bp.name, points: bp.points.length };
                            });
                            resolve();
                        });
                    });
                })
                .then(function () {
                    _currentBlueprintName = null;
                    _currentBlueprintPoints = null;
                    $("#currentBlueprint").text("Current blueprint: ");
                    _renderTable();
                    alert("Blueprint eliminado correctamente.");
                })
                .catch(function (err) {
                    console.error(err);
                    alert("No se pudo eliminar el blueprint.");
                });
        },


    };

})();

$(function () {
    if (typeof app.init === "function") {
        app.init();
    }

    $("#save-update").click(function () {
        app.saveCurrentBlueprint();
    });

    $("#create-blueprint").click(function () {
        app.createNewBlueprint();
    });

    $("#delete-blueprint").click(function () {
        app.deleteCurrentBlueprint();
    });

});


