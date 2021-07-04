package dev.softwarecats.scbchallenge.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    Integer code;
    String name;
    String phone;
}
