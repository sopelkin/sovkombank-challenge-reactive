package dev.softwarecats.scbchallenge.integrations.phones.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhonesResponse {
    private List<String> phones = new ArrayList<>();
}

