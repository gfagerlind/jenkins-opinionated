# jenkins-opinionated

TODO - Work in progress, not really mature

The repo is divided into two parts:
The [jenkins_server_bootstrap](jenkins_server_bootstrap) part and the demo part (everything else).

The jenkins_server_bootstrap is to bootstrap the demo - its not part of it!

There might be nicer ways and better patterns to bootstrap jenkins.

## TLDR - how to run the demo
You need to have docker installed, running and current user in the docker group.
Then just run:
`jenkins_server_bootstrap/bootstrap.sh`
And you should get a server on `localhost:8123`, login is `admin:admin`.
Commit changes to this repo, and rerun the jobs.

## So - what are the opinions?

### CI user is just a developer with an appetite for the mundane
Make it so any developer with the right credentials can do anything, and then make the CI system one of the any developers.

[Here](./reg.sh) is a [hacky example](./reg.mak) on how you can define the CI dependencies *outside* of the concept of jenkins, and then have jenkins being a secondary citizen that derives the work it needs to be done from the definitions of the primary citizen - the developer.

Strongly avoid having tests and builds that *only* work in the CI system, for example say that you have a rig computer, and you attach it to your jenkins server - then DO NOT hook it up to be directly triggered from the corresponding CI step that wants to run the test - instead, do the round about way of creating a mechanism in the repo to trigger a test with "current checked out git revision" on the specific rig - then *any developer* can use it, and the CI system is just any developer, so it can too.

In extension this means that only define *jenkins specific* things in [libs](./libs) - if it can reasonable be pushed to the local developer environment do it.

### Use jenkins core
Use jenkins, its the boring vanilla!
If you can find something more boring and more vanilla, use that instead.

Use [jenkins plugins](jenkins_server_bootstrap/plugins.txt), but only the most tried and true and old ones.
### Do not use a lot of jenkins jobs
In this setup you could, in theory run multiple repos, with multiple branches, in both a check,
gate and post-merge context, all in one single job.

That might be a bit *excessive* but the point is that you *can*.

You can of course decide to split up the cake how ever you want.
> *_NOTE:_* Current proposal to manage credentials implies multiple jobs for that)

### Do not configure the jobs in the GUI (nor anything else)
This goes without saying. Jenkins is very easy to mess up by allowing any kind of important configuration.
### Put the "configuration as code" together with the source code of the product
This is why this example uses as little [JCasC](jenkins_server_bootstrap/jenkins.yaml) as possible - its a means to an end.

The idea is that all the features are created and defined by the development community,
you can as a developer, in this system create your own new "gocd" or "zuul", test it and deploy it just like any regular product change.

### Hand over "emergency valve" functions, like priority etc to the development community
In these examples you can quite easily put your own change set on higher priority up until merge,
if you want, etc - this is a good thing. Either trust, or fix, your community.
### Do not share resources between communities
Avoid sharing resources across communities.
The definition of a community is a set of people with a shared goal, shared priorities and a shared
timeline.

## So - what are the features?

### zuulesque dependent gate
As in *"the code needs to be tested exactly as how it will be merged, so if someone is before you in line to merge, run your test on the speculation that they are successful. If they fail, restart your tests"*

[Here](libs/DependentGate.groovy)
### gocdesque latest first
As in *"run the pipeline stages on the latest candidate, leapfrogging over candidates that are valid, but older"*

In this implementation you can use multiple frogs, if you want (as in allowing X number of parallel executions of each stage).

[Here](libs/StageWithWip.groovy)
### dynamic, consistent and atomic modifications of the CI system
Together with the product code!
There is even a hacky [library system](libs/ImportFile.groovy) since its quite finicky to get true atomic behaviour of the regular jenkins groovy library plugin.
### implicit support for variants
As the product lifetime goes on, usually you find yourself in a situation,
where you want to keep an old release "alive", and keep CI working "as it was".

This is of course quite tricky, and in essence, the only way to be knowing if its alive
is to keep running it from time to time, but at least this setup should plausibly support you with that.

### global resource priority
Ie. the check, gate and post-merge all share resources and you decide on how to prioritize between them.

[Here](libs/NodeWithPriority.groovy)
### consistent definition of CI activities between check, gate and post-merge
Eg. so you feel confident that what worked in check will work in gate,
what worked in gate will work in post-merge, and what has been tested to work in post-merge will untouched work in check.
### dynamic resources and queues
Ie. change the amount of resources on the fly.

[Here](libs/NodeWithPriority.groovy)
### most of the functionality at equal access level
Ie. avoid *admin accounts* as much as possible, just have the *developer* level - if you are allowed to change the product, you are allowed to change CI.
### separate access of eg. check, gate and post-merge
By either using two different jobs (folders) or two different servers (if you don't trust yourself)

[Here](top.groovy#L4)
### leverage the jenkins ecosystem
For example out of the box support for all kinds of execution environments,
SCMs and notification tools.

Be careful though, only use what is really needed - each additional plugin is an additional risk.
### tinkerability
One of the core goals of this demo is to showcase how much can be achieved in "user space",
without breaking the core system (jenkins).
### using as few jenkins concepts (and plugins) as possible
To leave those degrees of freedom for the inevitable new problem or requirement that comes around the corner.
This includes:
* Creating multiple jobs,
* Running multiple jenkins instances,
* Writing or using more exotic plugins.
### define and verify your CI dependency tree without jenkins
By using a pattern similar to [reg.sh](./reg.sh)/[reg.mak](./reg.mak),
you can allow the developers to add new tests, rules for when and where those tests should be run,
and be able to verify, locally, that they indeed got it right.

### some less opinionated features
* [credentials](top.groovy#L4)
* [dynamic parameters](libs/DeclareParameter.groovy)
* A weak, but surprisingly competitive [visualisation](libs/TypicalJob.groovy#L16)
* A minor timeout [example](libs/TypicalJob.groovy#L23)
* A minor post run cleanup [example](libs/TypicalJob.groovy#L29)

## And what is TODO?
### Elaborate on more stuff wrt. how to think about CI/CD systems, with jenkins in particular
* Storing logs, jobs.
* Setting up nodes, containers, VMs.
### Polish
But the goal of the demo is not to have something to polished, rather to show the flexibility.

But as a hint, instead of having a bunch of business logic in [top.groovy](top.groovy),[dep.groovy](dep.groovy), [check.groovy](check.groovy), [postmerge.groovy](postmerge.groovy), you could transfer that logic into eg. [reg.mak](reg.mak).

The flow would then be that jenkins will ask [reg.mak](./reg.mak) to define:
* What scope to run on which CI event (PR, merge, etc).
* What tests belongs to what scope.
* What, and in what order, subscopes is part of a scope (think how postmerge works)
* What the WIP limit should be

That could then be updated and verified (offline) by any committed developer.

### The rebase and merge details of the dependent gate
But it will be easy I think

[Rebase](dep.groovy#L9)

[Merge](dep.groovy#L37)
