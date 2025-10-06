var apimock = (function () {

    var mockdata = {};

    mockdata["JohnConnor"] = [
        {author: "JohnConnor", name: "house", points: [{x: 50, y: 50}, {x: 150, y: 50}, {x: 100, y: 150}]},
        {author: "JohnConnor", name: "gear", points: [{x: 60, y: 60}, {x: 120, y: 60}, {x: 90, y: 120}]}
    ];

    mockdata["SarahConnor"] = [
        {author: "SarahConnor", name: "bridge", points: [{x: 10, y: 10}, {x: 200, y: 10}, {x: 200, y: 50}, {x: 10, y: 50}]},
        {author: "SarahConnor", name: "circle", points: [{x: 70, y: 70}, {x: 90, y: 70}, {x: 110, y: 90}, {x: 90, y: 110}, {x: 70, y: 90}]}
    ];

    mockdata["KyleReese"] = [
        {author: "KyleReese", name: "triangle", points: [{x: 20, y: 20}, {x: 100, y: 20}, {x: 60, y: 120}]},
        {author: "KyleReese", name: "rocket", points: [{x: 30, y: 30}, {x: 60, y: 10}, {x: 90, y: 30}, {x: 60, y: 80}]},
        {author: "KyleReese", name: "base", points: [{x: 0, y: 0}, {x: 150, y: 0}, {x: 150, y: 150}, {x: 0, y: 150}]}
    ];

    return {
        getBlueprintsByAuthor: function (author, callback) {
            callback(mockdata[author]);
        },

        getBlueprintsByNameAndAuthor: function (author, name, callback) {
            var bps = mockdata[author];
            if (bps) {
                var blueprint = bps.find(function (bp) {
                    return bp.name === name;
                });
                callback(blueprint);
            }
        }
    };

})();
