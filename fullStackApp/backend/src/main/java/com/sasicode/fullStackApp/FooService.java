package com.sasicode.fullStackApp;

public class FooService {

    private final FullStackAppApplication.Foo foo;

    public FooService(FullStackAppApplication.Foo foo) {
        this.foo = foo;
    }

    String getFooName() {
        return foo.name();
    }
}
