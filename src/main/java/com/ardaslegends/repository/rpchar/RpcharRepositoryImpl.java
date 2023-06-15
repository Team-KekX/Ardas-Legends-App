package com.ardaslegends.repository.rpchar;

import com.ardaslegends.domain.QRPChar;
import com.ardaslegends.domain.RPChar;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Objects;

public class RpcharRepositoryImpl extends QuerydslRepositorySupport implements RpcharRepositoryCustom {
   public RpcharRepositoryImpl() {
        super(RPChar.class);
    }

    @Override
    public Slice<RPChar> queryAll(Pageable pageable) {
       Objects.requireNonNull(pageable, "Pageable must not be null!");
       QRPChar qRpchar = QRPChar.rPChar;

       val result = from(qRpchar)
               .limit(pageable.getPageSize())
               .offset(pageable.getOffset())
               .fetchResults();

       val totalCount = result.getTotal();
       val rpchars = result.getResults();

       return new SliceImpl<>(rpchars, pageable, (pageable.getOffset() < totalCount));


    }
}
