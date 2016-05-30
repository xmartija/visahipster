(function() {
    'use strict';
    angular
        .module('visahipsterApp')
        .factory('Transaction', Transaction);

    Transaction.$inject = ['$resource'];

    function Transaction ($resource) {
        var resourceUrl =  'api/transactions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
