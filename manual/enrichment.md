# Enrichment Functions

## Linking

There are four ways to configure the linking enrichment function:

1. provide Path to LIMES config file as parameter
2. embed the whole rdf configuration for limes in the deer config
3. just give a link to a dataset and optional restrictions using parameters, in this case we use WOMBAT to learn LS automatically
4. give no configuration at all, in this case we assume you have a deduplication task, e.g. the input model to the enrichment function is both source and target for linking. In this case learning using wombat is used. No preprocessing is being done and just the standard measures are used for learning. If you need more control over the parameters of wombat, use method 1 or 2.

