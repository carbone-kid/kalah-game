angular.module('angApp', []).controller('MainController', function($scope, $http) {

    $scope.initData = function () {
        $scope.data = {};
        $scope.data.player = {
            uid: '-1',
            name: ''
        };
        $scope.data.availableGames = [];
        $scope.data.joinGameRequest = {
            playerUid: -1,
            opponentName: ""
        };
        $scope.data.game = {
            created: false,
            joined: false,
            winner: "",
            gameState: {
                playerKalah: 0,
                opponentKalah: 0,
                pits: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                gameOver: false,
                whosTurn: "",
                started: false
            },
            moveRequest: {
                playerUid: -1,
                pit: -1
            }
        };
        $scope.data.playerPitsClass = "active-pit";
        $scope.data.opponentPitsClass = "inactive-pit";

        $scope.data.errorMessage = "";
    };

    var errorHandler = function(response) {
        $scope.data.errorMessage = response.data;
    };

    $scope.loginIfEnterPressed = function ($event) {
        var keyCode = $event.which || $event.keyCode;
        if (keyCode === 13) {
            $scope.login();
        }
    };

    $scope.login = function () {
        $http.post('/login', $scope.data.player).
        then(function(response) {
            $scope.data.player = response.data;
        }, errorHandler);
    };

    $scope.logout = function () {
        $http.post('/logout', $scope.data.player).
        then(function(response) {
            $scope.initData();
        }, errorHandler);
    };

    $scope.createGame = function () {
        $http.put('/createGame', $scope.data.player).
        then(function(response) {
            $scope.data.game.gameState = response.data;
            $scope.data.game.created = true;
        }, errorHandler);
    };

    $scope.getAvailableGames = function () {
        $http.get('/getAvailableGames').
        then(function(response) {
            $scope.data.availableGames = response.data;
        }, errorHandler);
    };

    $scope.joinGame = function (opponentName) {
        $scope.data.joinGameRequest.playerUid = $scope.data.player.uid;
        $scope.data.joinGameRequest.opponentName = opponentName;

        $http.post('/joinGame', $scope.data.joinGameRequest).
        then(function(response) {
            $scope.data.game.gameState = response.data;
            $scope.data.game.joined = true;
        }, errorHandler);
    };

    $scope.move = function (pit) {
        $scope.data.game.moveRequest.pit = pit;
        $scope.data.game.moveRequest.playerUid = $scope.data.player.uid;

        $http.post('/move', $scope.data.game.moveRequest).
        then(function(response) {
            if($scope.isPitsDifferent($scope.data.game.gameState.pits, response.data.pits)) {
                $scope.playPutToPit();
            }

            $scope.data.game.gameState = response.data;
        }, errorHandler);
    };

    $scope.getGameState = function () {
        $http.post('/getGameState', $scope.data.player).
        then(function(response) {
            if($scope.isPitsDifferent($scope.data.game.gameState.pits, response.data.pits)) {
                $scope.playPutToPit();
            }

            $scope.data.game.gameState = response.data;
        }, errorHandler);
    };

    $scope.isPitsDifferent = function(pitsOld, pitsNew) {
        for(var i=0; i<12; i++) {
            if(pitsNew[i] != pitsOld[i]) {
                return true;
            }
        }
        return false;
    };

    $scope.playPutToPit = function() {
        var a = new Audio('/sounds/put_to_pit.wav');
        a.play();
    };

    $scope.playWin = function() {
        var a = new Audio('/sounds/you_win.wav');
        a.play();
    };

    $scope.playLose = function() {
        var a = new Audio('/sounds/you_lose.wav');
        a.play();
    };

    $scope.findTheWinner = function() {
        if($scope.data.game.gameState.playerKalah > $scope.data.game.gameState.opponentKalah) {
            $scope.data.game.winner = "You Win!"
        }
        else if($scope.data.game.gameState.playerKalah < $scope.data.game.gameState.opponentKalah) {
            $scope.data.game.winner = "You Lose!";
        }
        else {
            $scope.data.game.winner = "You were equally strong...";
        }
    };

    var init = function () {
        $scope.initData();

        setInterval(function(){
            if(!$scope.data.game.created && !$scope.data.game.joined) {
                $scope.getAvailableGames();
            }
        }, 2000);

        setInterval(function(){
            if($scope.data.game.created || $scope.data.game.joined) {
                $scope.getGameState();

                if($scope.data.game.gameState.gameOver) {
                    $scope.data.playerPitsClass = "inactive-pit";
                    $scope.data.opponentPitsClass = "inactive-pit";
                    $scope.findTheWinner();
                }
                else if($scope.data.game.gameState.whosTurn == $scope.data.player.name) {
                    $scope.data.playerPitsClass = "active-pit";
                    $scope.data.opponentPitsClass = "inactive-pit";
                }
                else {
                    $scope.data.playerPitsClass = "inactive-pit";
                    $scope.data.opponentPitsClass = "active-pit";
                }
            }
        }, 2000);
    };

    init();
});
