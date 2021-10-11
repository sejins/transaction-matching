package com.jingeore.matching;

import com.jingeore.account.Account;
import com.jingeore.product.Product;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinishedMatchingInfo {

    private Product product;

    private Account opposite;

    private Long finishedMatchingId;

    private EndStatus status;
}
