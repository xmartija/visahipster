(function() {
    'use strict';

    angular
        .module('visahipsterApp')
        .factory('BankAccountSearch', BankAccountSearch);

    BankAccountSearch.$inject = ['$resource'];

    function BankAccountSearch($resource) {
        var resourceUrl =  'api/_search/bank-accounts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
