package com.ardaslegends.repository.rpchar;

import com.ardaslegends.domain.QClaimBuild;
import com.ardaslegends.domain.QRPChar;
import com.ardaslegends.domain.RPChar;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;

@Slf4j
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

    @Override
    public List<RPChar> findRpCharsByNames(String[] names) {
        log.debug("Querying rpchars by names: {}", names);
        Objects.requireNonNull(names, "Names must not be null");
        QRPChar qRpChar = QRPChar.rPChar;

        log.trace("Executing query");
        val fetchedRpChars = from(qRpChar)
                .where(qRpChar.name.in(names))
                .stream().toList();

        log.debug("Queried rpchars: [{}]", fetchedRpChars);
        return fetchedRpChars;
    }


}
