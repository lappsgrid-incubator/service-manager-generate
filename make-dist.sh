#!/bin/sh
set -eu

CWD=`dirname $0`
VERSION=`cat VERSION`
JAR=smg-$VERSION.jar
DIST=$CWD/target/dist
OUTDIR=$DIST/smg-$VERSION
SCRIPT=$OUTDIR/smg
TGZ=smg-$VERSION.tgz

echo "Creating distribution for smg v."$VERSION

if [ -e $OUTDIR ] ; then
	rm -rf $OUTDIR
fi
mkdir -p $OUTDIR

cat smg | sed 's/${VERSION}/'$VERSION'/' > $SCRIPT
chmod ug+x $SCRIPT
cp target/$JAR $OUTDIR
#cp src/test/resources/*.config src/test/resources/*.properties $OUTDIR
cd $DIST
tar -czf $TGZ smg-$VERSION
