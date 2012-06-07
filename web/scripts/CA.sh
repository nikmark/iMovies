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


cp_pem() {
    infile=$1
    outfile=$2
    bound=$3
    flag=0
    exec <$infile;
    while read line; do
	if [ $flag -eq 1 ]; then
		echo $line|grep "^-----END.*$bound"  2>/dev/null 1>/dev/null
		if [ $? -eq 0 ] ; then
			echo $line >>$outfile
			break
		else
			echo $line >>$outfile
		fi
	fi

	echo $line|grep "^-----BEGIN.*$bound"  2>/dev/null 1>/dev/null
	if [ $? -eq 0 ]; then
		echo $line >$outfile
		flag=1
	fi
    done
}

usage() {
 echo "usage: $0 -newreq|-sign|-verify" >&2
}
SSLEAY_CONFIG="-config /etc/ssl/openssl.cnf"
CATOP="/etc/ssl/CA_iMovies"
CACERT="iMoviesCert.pem"
if [ -z "$OPENSSL" ]; then OPENSSL=openssl; fi

if [ -z "$DAYS" ] ; then DAYS="-days 365" ; fi	# 1 year
CADAYS="-days 1095"	# 3 years
REQ="$OPENSSL req $SSLEAY_CONFIG"
CA="$OPENSSL ca $SSLEAY_CONFIG"
VERIFY="$OPENSSL verify"
X509="$OPENSSL x509"
PKCS12="openssl pkcs12"

serialNumber(){
 $X509 -in $1 -serial -noout 
}
# if [ -z "$CATOP" ] ; then CATOP=./demoCA ; fi
#CAKEY=./cakey.pem
#CAREQ=./careq.pem
#CACERT=./cacert.pem

RET=0

while [ "$1" != "" ] ; do
case $1 in
-\?|-h|-help)
    usage
    exit 0
    ;;
# -newcert)
#    # create a certificate
#    $REQ -new -x509 -keyout newkey.pem -out newcert.pem $DAYS
#    RET=$?
#    echo "Certificate is in newcert.pem, private key is in newkey.pem"
#    ;;
-newreq)
    # create a certificate request
    $REQ -new -keyout /etc/ssl/CA_iMovies/private/$3.key -passin file:$4 -passout file:$4 -out /etc/ssl/CA_iMovies/$3.csr $DAYS -subj "$2"
    RET=$?
    echo "Request is in $3.csr, private key is in $3.key"
    ;;
# -newreq-nodes) 
#    # create a certificate request
#    $REQ -new -nodes -keyout newreq.pem -out newreq.pem $DAYS
#    RET=$?
#    echo "Request (and private key) is in newreq.pem"
#    ;;
# -newca)
    # if explicitly asked for or it doesn't exist then setup the directory
    # structure that Eric likes to manage things
#    NEW="1"
#    if [ "$NEW" -o ! -f ${CATOP}/serial ]; then
#	# create the directory hierarchy
#	mkdir -p ${CATOP}
#	mkdir -p ${CATOP}/certs
#	mkdir -p ${CATOP}/crl
#	mkdir -p ${CATOP}/newcerts
#	mkdir -p ${CATOP}/private
#	touch ${CATOP}/index.txt
#    fi
#    if [ ! -f ${CATOP}/private/$CAKEY ]; then
#	echo "CA certificate filename (or enter to create)"
#	read FILE
#
#	# ask user for existing CA certificate
#	if [ "$FILE" ]; then
#	    cp_pem $FILE ${CATOP}/private/$CAKEY PRIVATE
#	    cp_pem $FILE ${CATOP}/$CACERT CERTIFICATE
#	    RET=$?
#	    if [ ! -f "${CATOP}/serial" ]; then
#		$X509 -in ${CATOP}/$CACERT -noout -next_serial \
#		      -out ${CATOP}/serial
#	    fi
#	else
#	    echo "Making CA certificate ..."
#	    $REQ -new -keyout ${CATOP}/private/$CAKEY \
#			   -out ${CATOP}/$CAREQ
#	    $CA -create_serial -out ${CATOP}/$CACERT $CADAYS -batch \
#			   -keyfile ${CATOP}/private/$CAKEY -selfsign \
#			   -extensions v3_ca \
#			   -infiles ${CATOP}/$CAREQ
#	    RET=$?
#	fi
 #   fi
 #   ;;
# -xsign)
#    $CA -policy policy_anything -infiles newreq.pem
#    RET=$?
#    ;;

-pkcs12)
    if [ -z "$2" ] ; then
	CNAME="MyCert"
    else
	CNAME="$2"
    fi
    $PKCS12 -passin pass:$5 -passout pass:$4 -in /etc/ssl/CA_iMovies/newcerts/$3.pem -inkey $CATOP/private/$3.pem -certfile ${CATOP}/$CACERT -out /var/lib/tomcat7/webapps/iMovies/pkcs12/$3.p12 -export -name "$CNAME"
    RET=$?
    exit $RET
    ;;

-sign|-signreq)
    $CA -passin file:$CATOP/private/pass -policy policy_anything -batch -in $CATOP/$2.csr
#-passin pass:imovies -passout:imovies -policy policy_anything
#-policy policy_anything -passout pass:imovies
    RET=$?
    serial = `$X509 -in /var/lib/tomcat7/webapps/iMovies/certs/cert/$3.pem -serial -noout | cut -f 2 -d = `
#    name =  `/var/lib/tomcat7/webapps/iMovies/certs/cert/$3.pem | cut -f 8 -d / | cut -f 1 -d . `
    mv /var/lib/tomcat7/webapps/iMovies/certs/cert/$3.pem /var/lib/tomcat7/webapps/iMovies/certs/cert/$3_$serial.pem
    #cat newcert.pem
    #echo "Signed certificate is in /var/lib/tomcat7/webapps/iMovies/certs/cert/$3_$serial.pem"
    #rm $1
    ;;
# -signCA)
#    $CA -policy policy_anything -out newcert.pem -extensions v3_ca -infiles newreq.pem
#    RET=$?
#    echo "Signed CA certificate is in newcert.pem"
#    ;;
-signcert)
    echo "Cert passphrase will be requested twice - bug?"
    $X509 -x509toreq -in newreq.pem -signkey newreq.pem -out tmp.pem
    $CA -policy policy_anything -out newcert.pem -infiles tmp.pem
    RET=$?
    cat newcert.pem
    echo "Signed certificate is in newcert.pem"
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
