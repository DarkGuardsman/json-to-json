package com.darkguardsman.json.remap.fakes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FakeNumberNode<N extends Number> {
    private N number;
}
