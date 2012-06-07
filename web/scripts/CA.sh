#!/bin/sh
#
# CA - wrapper around ca to make it easier to use ... basically ca requires
#      some setup stuff to be done before you can use it and this makes
#      things easier between now and when Eric is convinced to fix it :-)
#
# CA -newca ... will setup the right stuff
# CA -newreq ... will generate a certificate request
# CA -sign ... will sign the generated request and output
#
# At the end of that grab newreq.pem and newcert.pem (one has the key
# and the other the certificate) and cat them together and that is what
# you want/need ... I'll make even this a little cleaner later.
#
#
# 12-Jan-96 tjh    Added more things ... including CA -signcert which
#                  converts a certificate to a request and then signs it.
# 10-Jan-96 eay    Fixed a few more bugs and added the SSLEAY_CONFIG
#                  environment variable so this can be driven from
#                  a script.
# 25-Jul-96 eay    Cleaned up filenames some more.
# 11-Jun-96 eay    Fixed a few filename missmatches.
# 03-May-96 eay    Modified to use 'ssleay cmd' instead of 'cmd'.
# 18-Apr-96 tjh    Original hacking
#
# Tim Hudson
# tjh@cryptsoft.com
#

# default openssl.cnf file has setup as per the following
# demoCA ... where everything is stored



usage() {
 echo "usage: $0 -newreq|-sign|-verify" >&2
}
SSLEAY_CONFIG="-config /etc/ssl/openssl.cnf"
CATOP="/etc/ssl/CA_iMovies"
CACERT="iMoviesCert.pem"

OPENSSL="openssl"
DAYS="-days 365"	# 1 year
CADAYS="-days 1095"	# 3 years
REQ="$OPENSSL req $SSLEAY_CONFIG"
CA="$OPENSSL ca $SSLEAY_CONFIG"
VERIFY="$OPENSSL verify"
X509="$OPENSSL x509"
PKCS12="openssl pkcs12"

RET=0

while [ "$1" != "" ] ; do
case $1 in
-\?|-h|-help)
    usage
    exit 0
    ;;

-newreq)
    # create a certificate request
#SUB=\"$2\"
echo -e "\n\n"
echo $2
echo -e "\n\n"
$OPENSSL genrsa -out /var/lib/tomcat7/webapps/iMovies/certs/$3.key 1024 -passin file:$4 -passout file:$4
$REQ -new -key /var/lib/tomcat7/webapps/iMovies/certs/$3.key -out /var/lib/tomcat7/webapps/iMovies/certs/$3.csr $DAYS -passin file:$4 -passout file:$4 -subj "$2"
   # $REQ -new -keyout $CATOP/private/$3.key -passin file:$4 -passout file:$4 -out $CATOP/$3.csr $DAYS -subj "$2"
    RET=$?
    echo "Request is in $3.csr, private key is in $3.key"
    ;;


-pkcs12)
    if [ -z "$2" ] ; then
	CNAME="MyCert"
    else
	CNAME="$2"
    fi
    $PKCS12 -passin pass:$5 -passin pass:$4 -in $CATOP/newcerts/$3.pem -inkey /var/lib/tomcat7/webapps/iMovies/certs/$3.key -certfile ${CATOP}/$CACERT -out /var/lib/tomcat7/webapps/iMovies/pkcs12/$3.p12 -export -name "$CNAME"
    RET=$?
    exit $RET
    ;;

-sign|-signreq)
    $CA  -policy policy_anything -passin file:$CATOP/private/pass -policy policy_anything -batch -in /var/lib/tomcat7/webapps/iMovies/certs/$2.csr
    RET=$?
    ;;

-verify)
    shift
    if [ -z "$1" ]; then
	    $VERIFY -CAfile $CATOP/$CACERT $2
	    RET=$?
    else
	for j
	do
 	    $VERIFY -CAfile $CATOP/$CACERT $j
	    if [ $? != 0 ]; then
		    RET=$?
	    fi
	done
    fi
    exit $RET
    ;;

-revoke)
   
   $CA -revoke $2 -passin file:$CATOP/private/pass
    RET=$?
	;;
-revcanc)
    $CA -revoke $2 -passin file:$CATOP/private/pass
    rm $2 
    RET=$?
   ;;

 *)
    echo "Unknown arg $i" >&2
    usage
    exit 1
    ;;
esac
shift
done
exit $RET
