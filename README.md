# paramount

Paramount provides logging a message with particular details on some expression of code. 

It is largely inspired by the `time` macro in clojure core. 
I wanted to add more things to the printed message, such as: 

- Printing the result of the expression.
  - Being able to easily set the `*print-length*` and `*print-level*` bindings of the printed result. 
- Printing the source of the expression being evaluated.
- The elapsed time taken to evaluate the expression. 
- The time at which the log is created.
