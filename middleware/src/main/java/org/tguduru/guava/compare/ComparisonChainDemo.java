package org.tguduru.guava.compare;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Demonstrates the usage of guava's {@link com.google.common.collect.ComparisonChain}. The
 * {@link com.google.common.collect.ComparisonChain} helps in implementation of {@link Comparable#compareTo(Object)}
 * easily.
 * @author Guduru, Thirupathi Reddy
 */
public class ComparisonChainDemo {
    public static void main(final String[] args) {
        final Person person1 = new Person("John", 100);
        final Person person2 = new Person("Blake", 200);
        System.out.println(person1.compareTo(person2));
        System.out.println(person1.compareTo(person1));
    }

    static class Person implements Comparable<Person> {
        String name;
        long id;

        private Person(final String name, final long id) {
            this.name = name;
            this.id = id;
        }

        private String getName() {
            return name;
        }

        private Person setName(final String name) {
            this.name = name;
            return this;
        }

        private long getId() {
            return id;
        }

        private Person setId(final long id) {
            this.id = id;
            return this;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            final Person person = (Person) o;
            return Objects.equal(id, person.id) && Objects.equal(name, person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, id);
        }

        public int compareTo(final Person o) {
            // this eliminates lot of boiler plate code with comparison
            return ComparisonChain.start().compare(getId(), o.getId()).compare(getName(), o.getName()).result();
        }
    }
}
