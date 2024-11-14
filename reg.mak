TARGETS := a b c d e
SCOPES := check gate post-merge post-post-merge
# You could for sure dynamically decide on the scope depending on
# the changed files if you want
check: a b c

gate: b c d

post-merge: b c d e
post-post-merge: a

$(TARGETS): NODE=label
a b c d: TIMEOUT=60
e: TIMEOUT=60

ifndef OPINIONATED
$(TARGETS):
	@echo starting $@
	@sleep 10
	@echo finished $@
else
.NOTPARALLEL:
$(TARGETS):
	@echo '$@: { shJob($(TIMEOUT),"$(NODE)", "./reg.sh $@") },'
$(TARGETS): first
first:
	@echo 'return ['
$(SCOPES):
	@echo ']'
endif

