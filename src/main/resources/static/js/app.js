// app.js
var app = (function () {

    var _author = null;
    var _blueprints = [];

    function _renderTable() {
        $("#blueprintsTable tbody").empty();
        _blueprints.map(function (bp) {
            var row = `<tr>
                         <td>${bp.name}</td>
                         <td>${bp.points}</td>
                         <td><button class="btn btn-primary">Open</button></td>
                       </tr>`;
            $("#blueprintsTable tbody").append(row);
        });

        var total = _blueprints.reduce(function (acc, bp) {
            return acc + bp.points;
        }, 0);

        $("#totalPoints").text(total);
    }

    return {
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

            apimock.getBlueprintsByAuthor(_author, function (data) {
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

                _renderTable();
            });
        }
    };

})();
