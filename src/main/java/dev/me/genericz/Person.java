package dev.me.genericz;

import org.springframework.data.annotation.Id;

record Person(@Id Long id, String name, int age, String username) {
}
