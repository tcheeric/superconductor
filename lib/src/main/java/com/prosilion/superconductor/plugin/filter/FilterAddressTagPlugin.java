package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.AddressTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.AddressTag;
import org.springframework.stereotype.Component;

@Component
public class FilterAddressTagPlugin<T extends AddressTagFilter<AddressTag>, U extends GenericEvent> extends AbstractTagFilterPlugin<T, U, AddressTag> {
  public FilterAddressTagPlugin() {
    super(AddressTagFilter.FILTER_KEY, AddressTag.class);
  }
}
