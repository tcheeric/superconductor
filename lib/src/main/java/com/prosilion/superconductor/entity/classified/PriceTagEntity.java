package com.prosilion.superconductor.entity.classified;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.classified.PriceTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.PriceTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "price_tag")
// TODO: comprehensive unit test all parameter variants
public class PriceTagEntity extends AbstractTagEntity {
  private BigDecimal number;
  private String currency;
  private String frequency;

  public PriceTagEntity(@NonNull PriceTag priceTag) {
    super("price");
    this.number = priceTag.getNumber();
    this.currency = priceTag.getCurrency();
    this.frequency = priceTag.getFrequency();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new PriceTag(number, currency, frequency);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new PriceTagDto(new PriceTag(number, currency, frequency));
  }

  @Override
  @Transient
  public List<String> get() {
    return List.of(number.toString(),
        Optional.ofNullable(currency).toString(),
        Optional.ofNullable(frequency).toString());
  }
}
