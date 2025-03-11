package dev.me.genericz;

import org.springframework.data.annotation.Id;

record Person(@Id Long id, String name, Integer age, String username) {
}
