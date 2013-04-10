How to run:

There're three test files in engine.test package. Run each of them; they cover most of the cases that will be involvde in v.1. I used REAL Transducer in Unit Testing. It involves threads and race condition. Thus, I added several thread sleeping statements in each test. They all run well on my laptop. However, if any of the tests goes wrong, it's due to race conditions. Making the test thread sleep for a longer time will solve the problem.

Thank you very much.