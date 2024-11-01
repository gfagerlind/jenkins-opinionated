// Ok - so priority is not jenkins strong suite, ill give you that,
// never the less, the Lockable Resources plugin gives us something to work with
import org.jenkins.plugins.lockableresources.LockableResourcesManager as LRM

// create a resource and check the amount
@NonCPS
def createResource(resource_name, amount) {
    // these resources are not ephemeral
    def lrm = LRM.get()
    def intendedList = (1..amount).collect({resource_name + "${it}"})
    def currentList = lrm.getResourcesWithLabel(resource_name)
    def currentNames = currentList.collect({it.getName()})
    def createThese = intendedList.findAll {
        // filterout existing resources
        ! currentNames.contains(it)
    }
    def removeThese = currentList.findAll {
        ! intendedList.contains(it.getName())
    }
    // TODO: i think this is protected against race condition,
    // othewise put a lock *outside* of the NonCPS block
    createThese.each {
        echo "creating ${resource_name} at ${amount}"
        if (! lrm.createResourceWithLabel(it, resource_name)) {
            echo "failed to create ${it} ${resource_name}"
        }
    }
    lrm.removeResources(currentList.findAll {
        ! intendedList.contains(it.getName())
    })
    lrm.save()
}

def _queue(args, cls) {
    //name, wip, latest_is_greatest, priority
    def name = args.name
    def wip = args.wip
    def fwdargs = [:]
    if (args.latest_is_greatest) {
        fwdargs.inversePrecedence = true
        assert ! args.priority, "prio and inverse prio cannot be combined"
    }
    if (! args.latest_is_greatest && args.priority ) {
        fwdargs.priority = args.priority
    }
    if (wip > 1) {
        createResource(name, wip)
        lock([label: name, quantity: 1] + fwdargs) {
            cls.call()
        }
    } else {
        lock([resource: name] + fwdargs) {
            cls.call()
        }
    }
}
{->
    queue = { args, cls -> _queue(args, cls)}
}
