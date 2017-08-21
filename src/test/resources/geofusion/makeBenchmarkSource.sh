#!/bin/bash
#
# Benchmarking of geospatial fusion operator
# Generator for test input datasets
#
# First param: Number of resources in each dataset, each with an interlink
size=$1

# prepare prefixes for both benchmarking source files

cat << EOF > bmFirstSource.ttl
@prefix : <http://example.org/deer/geofusion/> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .
@prefix owl:     <http://www.w3.org/2002/07/owl#> .

EOF

cat << EOF > bmSecondSource.ttl
@prefix : <http://example.org/deer/geofusion/> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .

EOF

counter=0
while [ $counter -lt $size ]; do
	r1=$((counter*2+1));
	r2=$((counter*2+2));
	echo :r$r1 rdfs:label \"Place in first source\"\; geo:lat \"51.35\"\; geo:long \"12,45\"\; owl:sameAs :r$r2. >> bmFirstSource.ttl
	echo :r$r2 rdfs:label \"Place in second source\"\; geo:lat \"51.3487\"\; geo:long \"12.4512\". >> bmSecondSource.ttl
	let counter=counter+1;
done
