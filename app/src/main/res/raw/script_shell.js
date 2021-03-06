var env = env || {};
env.vars = env.vars || {};
var max_iter = 100;
var iter = 0;
var function_stack = [];
var old_exec = env.execute;
env.execute = function(next_fun) {
    function_stack.push(next_fun);
}
function_stack.push(entryFunction);

while (function_stack.length != 0) {
    var top_fun = function_stack.pop();
    top_fun(env);
    iter++;
    if (iter > max_iter) {
        floApi.errorMessage("Stack overflow error. Stack depth is " + max_iter);
        break;
    }
}
env.execute = old_exec;