class DependencyUpdateException extends Throwable {}
{ ->
    import_file('libs/StageWithWip.groovy')

    // find the first job older than `build` that is still running
    getLastRunningDependentGate = { build ->
        if (build == null)
            return null
        if (build.buildVariables.gateContext == null) {
            // if not a dependent job
            return getLastRunningDependentGate(build.getPreviousBuild());
        }
        if (build.result == null)
            return build
        if (build.result in ['FAILURE', 'ABORTED']) {
            return getLastRunningDependentGate(build.getPreviousBuild());
        }
        return null
    }

    // get the context (what dependency tree) of a job
    getGateContext = { watchedJob ->
        if (watchedJob) {
            return "${watchedJob.buildVariables.gateContext}\n${currentBuild.number}"
        } else {
            return "${currentBuild.number}"
        }
    }

    env.gateContext = ""
    // dependentGate(wip = How many parallel gate jobs) { the code}
    dependentGate = { wip = 1, testcls, mergecls ->
        // add assert that no one is using dependentGate inside dependent gate
        assert ! binding.hasVariable('dependentGatePassed'): "dependentGate inside dependentGate is not allowed"
        def dependentGatePassed = false
        def watchedJob = getLastRunningDependentGate(currentBuild.getPreviousBuild())
        env.gateContext = getGateContext(watchedJob)
        def checkDependency = {
            // check that we are watching the same job
            if (! watchedJob) {
                // if first job
                return
            }
            if ( watchedJob.result != 'SUCCESS' && getLastRunningDependentGate(currentBuild.getPreviousBuild()) != watchedJob) {
                watchedJob = getLastRunningDependentGate(currentBuild.getPreviousBuild())
            }
            if ( getGateContext(watchedJob) != env.gateContext ) {
                echo "dependency chain has changed, restart test, was ${env.gateContext}"
                env.gateContext = getGateContext(watchedJob)
                echo "is ${env.gateContext}"
                throw new DependencyUpdateException()
            }
        }
        def watchdog = {
            while ( ! dependentGatePassed) {
                sleep 5
                checkDependency()
            }
        }
        // if you are fancy, consider a dynamically increasing and decreasing resource.
        // I dont think it makes any sense at all; harder to understand
        stageWithWip(name: "DependentGate", latest_is_greatest: false, wip: wip) {
            if (watchedJob) {
                while (! dependentGatePassed) {
                    try {
                        parallel (
                            watchdog: { watchdog.call() },
                            failFast: true,
                            p: {
                                testcls.call()
                                dependentGatePassed = true
                            }
                        )
                        if (watchedJob) {
                            echo('wait for dependent job')
                            while ( watchedJob.result == null ) {
                                sleep 10
                            }
                            echo "dependent job finished, check watchdog a last time"
                            checkDependency()
                            mergecls()
                        }
                    } catch(DependencyUpdateException e) {
                        dependentGatePassed = false
                    }
                }
            } else {
                // no watchdogging and junk, just run the closure
                testcls.call()
            }
        }
    }
}
