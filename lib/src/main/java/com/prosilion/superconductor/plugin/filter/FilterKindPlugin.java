package com.prosilion.superconductor.plugin.filter;

import nostr.event.Kind;
import nostr.event.filter.KindFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

@Component
public class FilterKindPlugin<T extends KindFilter<Kind>, U extends GenericEvent> extends AbstractFilterPlugin<T, U> {
  public FilterKindPlugin() {
    super(T.FILTER_KEY);
  }
}
