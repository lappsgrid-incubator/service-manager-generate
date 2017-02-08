#!/bin/sh
set -eu

VERSION=`cat VERSION`
JAR=smg-$VERSION.jar
OUTDIR=./dist/smg-$VERSION
SMG=$OUTDIR/smg
TGZ=smg-${VERSION}.tgz 

echo "Creating distribution for smg v."$VERSION

if [ -e $OUTDIR ] ; then
	rm -rf $OUTDIR
fi
mkdir -p $OUTDIR

cat smg | sed 's/${XXXX}/'$VERSION'/' > $SMG
chmod ug+x $SMG
cp target/$JAR $OUTDIR
cp src/test/resources/*.config $OUTDIR

tar -czf $TGZ $OUTDIR
