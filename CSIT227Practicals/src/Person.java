
public abstract class Person {
    // TODO implement Person and its subclasses in other Java files

    private String name;
    private int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Hello, my name is " + name + ". ";
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}
