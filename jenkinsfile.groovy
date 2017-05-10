stage 'CI' 
node('master') {
	try{
notify('Started')
    git branch: 'master', 
        url: 'https://github.com/himanshubhardwaj87/solitaire.git'
    bat 'npm install'
    bat 'npm run test-single-run -- --browsers PhantomJS'
    step([$class: 'JUnitResultArchiver', 
          testResults: 'test-results/**/test-results.xml'])
}     
 catch (err) {
        notify("Error: ${err}")
        currentBuild.result = 'FAILURE'
    }  
}
stage 'Browser Testing'
parallel chrome:{
    runTests("Chrome")
}, firfox:{
    runTests("Firefox")
}
notify('Execution Complete')
 
def runTests(browser) {
    node {
		try{
//     bat 'del /S /Q *'
//     unstash 'everything'
     bat "npm run test-single-run -- --browsers ${browser}"

        step([$class: 'JUnitResultArchiver', 
              testResults: 'test-results/**/test-results.xml'])
    }
	 catch (err) {
        notify("Error: ${err}")
        currentBuild.result = 'FAILURE'
    }
	}
}
def notify(status){
    emailext (
      to: "himanshubhardwaj87@gmail.com",
      subject: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
    )
}