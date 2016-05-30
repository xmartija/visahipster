'use strict';

describe('Controller Tests', function() {

    describe('Transaction Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTransaction, MockBankAccount;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTransaction = jasmine.createSpy('MockTransaction');
            MockBankAccount = jasmine.createSpy('MockBankAccount');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Transaction': MockTransaction,
                'BankAccount': MockBankAccount
            };
            createController = function() {
                $injector.get('$controller')("TransactionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'visahipsterApp:transactionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
