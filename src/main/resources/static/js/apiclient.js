var apiclient = (function () {

    var apiUrl = "http://localhost:8080/blueprints";

    return {

        getBlueprintsByAuthor: function (author, callback) {
            $.get(apiUrl + "/" + author, function (data) {
                callback(data);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                console.error("Error obteniendo planos por autor:", textStatus, errorThrown);
                callback(null);
            });
        },

        getBlueprintsByNameAndAuthor: function (author, name, callback) {
            $.get(apiUrl + "/" + author + "/" + name, function (data) {
                callback(data);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                console.error("Error obteniendo plano:", textStatus, errorThrown);
                callback(null);
            });
        },

        updateBlueprint: function (author, blueprintName, blueprint, callback) {
            $.ajax({
                url: apiUrl + "/" + author + "/" + blueprintName,
                type: 'PUT',
                data: JSON.stringify(blueprint),
                contentType: "application/json"
            })
            .done(function () {
                callback(true);
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                console.error("Error actualizando plano:", textStatus, errorThrown);
                callback(false);
            });
        },

        createBlueprint: function (author, blueprint, callback) {
            $.ajax({
                url: apiUrl,
                type: 'POST',
                data: JSON.stringify(blueprint),
                contentType: "application/json"
            })
            .done(function () {
                callback(true);
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                console.error("Error creando plano:", textStatus, errorThrown);
                callback(false);
            });
        }

    };

})();
