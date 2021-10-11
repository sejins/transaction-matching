package com.jingeore.account;

import com.jingeore.matching.ReviewResultForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ReviewScoreService {

    public void addReview(ReviewScore reviewScore, ReviewResultForm reviewResultForm) {
        if (reviewResultForm.getPositive1() != null) {
            reviewScore.setPositive1(reviewScore.getPositive1() + 1);
        }
        if (reviewResultForm.getPositive2() != null) {
            reviewScore.setPositive2(reviewScore.getPositive2() + 1);
        }
        if (reviewResultForm.getPositive3() != null) {
            reviewScore.setPositive3(reviewScore.getPositive3() + 1);
        }
        if (reviewResultForm.getPositive4() != null) {
            reviewScore.setPositive4(reviewScore.getPositive4() + 1);
        }
        if (reviewResultForm.getNegative1() != null) {
            reviewScore.setPositive1(reviewScore.getPositive1() + 1);
        }
        if (reviewResultForm.getNegative2() != null) {
            reviewScore.setNegative2(reviewScore.getNegative2() + 1);
        }
        if (reviewResultForm.getNegative3() != null) {
            reviewScore.setNegative3(reviewScore.getNegative3() + 1);
        }
        if (reviewResultForm.getNegative4() != null) {
            reviewScore.setNegative4(reviewScore.getNegative4() + 1);
        }
    }


}
