package ca.cutterslade.edbafakac.db;

public interface NegatedSearchTerm extends SearchTerm {

  SearchTerm getNegatedTerm();
}
