/**
 * Author: Brent Barker
 */

var seautoApp = angular.module('seautoApp', ['ui.router']);


seautoApp.config(function($stateProvider, $urlRouterProvider) {
  
  
  var routes = new Array();
  routes.push('features');
  routes.push('pageObjects');
  routes.push('siteObjects');
  routes.push('codeOrg');
  routes.push('storyContext');
  routes.push('getStarted');
  routes.push('logging');
  routes.push('reports');
  routes.push('runTests');
  routes.push('contribute');
  routes.push('404');
  
  var scrollTo = ":scrollTo";
  for(var i = 0; i < routes.length; i++) {
    $stateProvider
      .state(routes[i], {
        url: '/' + routes[i] + scrollTo, 
        templateUrl : 'partials/' + routes[i] + '.html',
      });
  };
  
  $stateProvider
    .state('createTests', {
      url: '/createTests',
      views: {
        '' : { templateUrl: 'partials/createTests.html' },
        "stepExamples@createTests" : { templateUrl: 'partials/nestedViews/stepExamples.html' },
        "pageExamples@createTests" : { templateUrl: 'partials/nestedViews/pageExamples.html' }
      }
    })
    
    .state('stepObjects', {
      url: '/stepObjects',
      views: {
        '' : { templateUrl: 'partials/stepObjects.html' },
        "stepExamples@stepObjects" : { templateUrl: 'partials/nestedViews/stepExamples.html' }
      }
    })
  .state('intro', {
    url: '',
    templateUrl : 'partials/intro.html',
  })
  
  .state('sampleFiles', {
    url: '/sampleFiles',
    views: {
     '' : { templateUrl : 'partials/sampleFiles.html', },
     "configPkg@sampleFiles" : { templateUrl: 'partials/nestedViews/configPkg.html' },
    }
  })
  .state('configuration', {
    url: '/configuration' + scrollTo,
    views: {
     '' : { templateUrl : 'partials/configuration.html', },
     "configPkg@configuration" : { templateUrl: 'partials/nestedViews/configPkg.html' },
    }
  });
  
  // For any unmatched url, redirect to /404
  $urlRouterProvider.otherwise("/404");
  
  
  
});


//listen for scrollTo events
seautoApp.controller('stateChangeSuccessCtrl', ['$scope', function($scope) {
  
  $scope.$on('$stateChangeSuccess', function(evt, toState, toParams, fromState, fromParams) {
    //make formatting in <pre> tags nicely formatted.
    prettyPrint();
    var scrollVar = "#header";
    
    if (toParams.scrollTo){
      console.log("Scroll");
      scrollVar = toParams.scrollTo;
      scrollToElm(scrollVar);
    }
  });
}]);

function scrollToElm(selector)
{
    $("html, body").animate({ scrollTop: $(selector).offset().top }, "slow");
    return false;
}


