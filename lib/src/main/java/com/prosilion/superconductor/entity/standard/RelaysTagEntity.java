package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.RelaysTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.base.Relay;
import nostr.event.BaseTag;
import nostr.event.tag.RelaysTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "relays_tag")
// TODO: comprehensive unit test all parameter variants
public class RelaysTagEntity extends AbstractTagEntity {
  private String uri;

  public RelaysTagEntity(@NonNull RelaysTag relaysTag) {
    super("relays");
    this.uri = relaysTag.getRelays().getFirst().getUri();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new RelaysTag(new Relay(uri));
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new RelaysTagDto(new RelaysTag(new Relay(uri)));
  }

  @Override
  @Transient
  public List<String> get() {
    return List.of(uri);
  }
}
