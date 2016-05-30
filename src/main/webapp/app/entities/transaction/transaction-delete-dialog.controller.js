(function() {
    'use strict';

    angular
        .module('visahipsterApp')
        .controller('TransactionDeleteController',TransactionDeleteController);

    TransactionDeleteController.$inject = ['$uibModalInstance', 'entity', 'Transaction'];

    function TransactionDeleteController($uibModalInstance, entity, Transaction) {
        var vm = this;
        vm.transaction = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Transaction.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
