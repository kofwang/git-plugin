package hudson.plugins.git.extensions.impl.Clone;

def f = namespace(lib.FormTagLib);

f.entry(title:_("Efs clone"), field:"enable") {
    f.checkbox()
}
