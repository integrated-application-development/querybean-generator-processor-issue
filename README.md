# query-bean-generator-issue

This project illustrate an issue with the ebean query bean generator. Issue as of `ebean` version `15.8.2` can be reproduced by running `mvn compile` on this example project. Issue has been raised at ebean-orm/ebean#3582.

When generating querybeans along side specific other annotation processors (in this case [manifold-exceptions](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-exceptions)), the `querybean-generator` logs an error while trying to write `embedded.example.EbeanEntityRegister` and fails the build (error details below).

## Pre-`15.8.2` Behaviour

With or without `manifold-exception`, the generator just generate without any `[WARNING]` or `[ERROR]` logs.

## `15.8.2` Behaviour

*This behaviour can be reproduced with just `querybean-generator` upgraded to `15.8.2` while keeping other ebean dependencies at an older version (e.g. `15.8.1`). But for simplicity, this example set all ebean dependencies version at `15.8.2`.*

### Without `manifold-exception`

*This case can be reproduced in this sample project by commenting out `manifold-exception` at line `56` and `59-63` in `pom.xml`.*

At `querybean-generator` version `15.8.2` without `manifold-exception` added to the path processors, the querybeans generation logs the warning below but completes successfully. The compilation also completes sucessfully unless `-Werror` compilation flag is turned on.

```
[INFO] --- compiler:3.13.0:compile (default-compile) @ query-bean-embedded-example ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 3 source files with javac [forked debug deprecation target 17] to target\classes
[WARNING] File for type 'embedded.example.EbeanEntityRegister' created in the last round will not be subject to annotation processing.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### With `manifold-exception`

With `manifol-exception` added to the path processors, the querybeans generation logs the compilation error below and failed the build.

```
[INFO] --- compiler:3.13.0:compile (default-compile) @ query-bean-embedded-example ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 3 source files with javac [forked debug deprecation target 17] to target\classes
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] error: Failed to write EntityClassRegister error:java.lang.NullPointerException: Cannot invoke "java.util.Queue.add(Object)" because "this.deferred" is null stack:[jdk.compiler/com.sun.tools.javac.util.Log$DeferredDiagnosticHandler.report(Log.java:150), jdk.compiler/com.sun.tools.javac.util.Log.report(Log.java:660), jdk.compiler/com.sun.tools.javac.util.AbstractLog.warning(AbstractLog.java:163), jdk.compiler/com.sun.tools.javac.processing.JavacFiler.createSourceOrClassFile(JavacFiler.java:513), jdk.compiler/com.sun.tools.javac.processing.JavacFiler.createSourceFile(JavacFiler.java:435), io.ebean.querybean.generator.ProcessingContext.createWriter(ProcessingContext.java:399), io.ebean.querybean.generator.SimpleModuleInfoWriter.<init>(SimpleModuleInfoWriter.java:31), io.ebean.querybean.generator.Processor.writeModuleInfoBean(Processor.java:90), io.ebean.querybean.generator.Processor.process(Processor.java:53), jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment.callProcessor(JavacProcessingEnvironment.java:1023), jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment$DiscoveredProcessors$ProcessorStateIterator.runContributingProcs(JavacProcessingEnvironment.java:859), jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment$Round.run(JavacProcessingEnvironment.java:1265), jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment.doProcessing(JavacProcessingEnvironment.java:1404), jdk.compiler/com.sun.tools.javac.main.JavaCompiler.processAnnotations(JavaCompiler.java:1234), jdk.compiler/com.sun.tools.javac.main.JavaCompiler.compile(JavaCompiler.java:916), jdk.compiler/com.sun.tools.javac.main.Main.compile(Main.java:317), jdk.compiler/com.sun.tools.javac.main.Main.compile(Main.java:176), jdk.compiler/com.sun.tools.javac.Main.compile(Main.java:64), jdk.compiler/com.sun.tools.javac.Main.main(Main.java:50)]
[INFO] 1 error
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

The stack trace suggests that this issue happens when the underlying `JavacFiler` is trying to log the exact same `[WARNING]` observed above when running `querybean-generator` version `15.8.2` without `manifold-exceptions` during `JavacFiler.createSourceOrClassFile`. When attempting this, the logging framework found that its `DeferredDiagnosticHandler`'s `deferred` queue is `null` and threw an `NPE` which was captured and formatted into the `[ERROR]` seen above. A quick look at the code suggests that `deferred` queue is only set to `null` when `DeferredDiagnosticHandler.reportDeferredDiagnostics` is called to prevent accidental ongoing use.
