/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

define([
    'underscore',
    'find/app/router-constructor'
], function(_, RouterConstructor) {
    //noinspection LocalVariableNamingConventionJS
    var Router = RouterConstructor.extend({

        routes: _.extend({
            'find/search/suggest/:domain/:index/:reference': 'suggest'
        }, RouterConstructor.prototype.routes)
    });

    return new Router();
});
