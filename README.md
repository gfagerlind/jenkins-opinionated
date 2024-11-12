# jenkins-opinionated

## So - what are the opinions?

### Use jenkins core
Use jenkins, its the boring vanilla!


Use jenkins plugins, but only the most tried and true and old ones.
### Do not use a lot of jenkins jobs
In this setup you could, in theory run multiple repos, with multiple branches, in both a pregate,
gate and postmerge context, all in one single job.

That might be a bit *excessive* but the point is that you *can*.

You can of course decide to split up the cake how ever you want

### Do not configure the jobs in the gui (nor anything else)
This goes without saying. Jenkins is very easy to mess up by allowing any kind of important configuration.
### Put the "configuration as code" together with the source code of the product
This is why I dont use the groovy libs, nor jenkins casc - you could,
but i want to give as much power to power users as possible, and see what they come up with.

The idea is that all the features are created and defined by the development community,
you can as a developer, in this system create your own new "gocd" or "zuul", test it and deploy it just like any regular product change.

There need of course to be somekind of casc boilerplate - this is currently TODO

### Hand over "emergency valve" functions, like prio etc to the development community
In these examples you can quite easily put your own change set on higher priority up until merge,
if you want etc - this is a good thing. Either trust, or fix, your community.
### Do not share resources between communities
I do not like sharing resources cross communities.
The defintion of a community is a set of people with a shared goal, shared priorities and a shared
timeline.

## So - what are the features?

### zuulesque dependent gate
As in "the code needs to be tested exactly as how it will be merged, so if someone is before you in mergeline, run your test on the speculation that they are successful. If they fail, restart your tests"
### gocdesque latest first
As in "run the pipeline stages on the latest candidate, leapfrogging over candidates that are valid, but older"
In this implementation you can use multiple frogs, if you want (as in allowing X number of parallel executions of each stage).
### global resource priority
Ie. the check, gate and post-merge all share resources and you decide on how to prioritize between them.
### dynamic resources and queues
Ie. change the amount of resources on the fly.
### most of the functionality at equal access level
Ie. avoid *admin accounts* as much as possible, just have the *developer* level - if you are allowed to change the product, you are allowed to change ci.
### separate access of premerge and postmerge
By either using two different jobs or two different servers (if you dont trust yourself)
### leverage the Jenkins ecosystem
For example out of the box support for all kinds of execution environments,
scms and notification tools.

Be careful though, only use what is really needed.
### tinkerability
One of the core goals of this demo is to showcase how much can be achived in "user space",
without breaking the core system (Jenkins).

## And what is TODO?
### Examples of credential management, especially credential segregation
TODO
### A one click setup
You need to install the plugins and create the job right now

Somethings are not easy to decide in a demo though
### The rebase details of the dependent gate
But it will be easy I think
