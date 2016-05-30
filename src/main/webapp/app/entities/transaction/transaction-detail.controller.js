(function() {
    'use strict';

    angular
        .module('visahipsterApp')
        .controller('TransactionDetailController', TransactionDetailController);

    TransactionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Transaction', 'BankAccount'];

    function TransactionDetailController($scope, $rootScope, $stateParams, entity, Transaction, BankAccount) {
        var vm = this;
        vm.transaction = entity;
        
        var unsubscribe = $rootScope.$on('visahipsterApp:transactionUpdate', function(event, result) {
            vm.transaction = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
