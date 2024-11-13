// thing method gives you the id to link to the sublog
def getLocalUrlId(flowNode = null) {
    if(!flowNode) {
        flowNode = getContext(org.jenkinsci.plugins.workflow.graph.FlowNode)
    }
    while ( flowNode.parents) {
        if(flowNode instanceof org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode) {
            return flowNode.id
        }
        flowNode = flowNode.parents[0]
    }
}

// TODO: yeah, just add an optional node label and timeout or what ever you want.
def job(cmd) {
    def id = getLocalUrlId()
    def url = "${env.BUILD_URL}/pipeline-console/?selected-node=${id}"
    def log = "${env.BUILD_URL}/pipeline-console/log?nodeId=${id}"
    try {
        echo "running ${url}"
        node() {
            timeout(time: 1, unit: 'MINUTES') {
                cmd()
            }
        }
        echo "exit ${log}"
    } catch (hudson.AbortException e) {
        echo "${url} failed"
        throw e
    }
}

def _shJob(cmd) {
    job() {
        sh(cmd)
    }
}

def call() {
    shJob = {cmd -> _shJob(cmd)}
}

return this
