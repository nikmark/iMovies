#!/bin/bash

SSLEAY_CONFIG="-config /etc/ssl/openssl.cnf"
CATOP="/etc/ssl/CA_iMovies"
CACERT="iMoviesCert.pem"
DAYS="-days 365"


case $1 in
-newreq)
    # create a certificate request
echo -e "\n\n"
echo $2
echo -e "\n\n"
openssl genrsa -des3 -passout file:$4 -out $CATOP/private/$3.key 4096
openssl req -new -key $CATOP/private/$3.key -out $CATOP/certs/$3.csr -passin file:$4 -passout file:$4 -subj "$2"
echo "Request is in $3.csr, private key is in $3.key"
    ;;





-sign|-signreq)
openssl ca $SSLEAY_CONFIG -passin file:$CATOP/private/pass -batch -in $CATOP/certs/$2.csr -startdate $3 -enddate $4 -out /var/lib/tomcat7/webapps/iMovies/certs/$2.pem 
openssl x509 -in /var/lib/tomcat7/webapps/iMovies/certs/$2.pem -serial -noout | cut -f 2 -d = 
SERIAL=`openssl x509 -in /var/lib/tomcat7/webapps/iMovies/certs/$2.pem -serial -noout | cut -f 2 -d =` >>  $CATOP/private/seriale.txt
mv $CATOP/private/$2.key $CATOP/private/$SERIAL.key
mv $CATOP/certs/$2.csr $CATOP/certs/$SERIAL.csr
rm /var/lib/tomcat7/webapps/iMovies/certs/$2.pem
    ;;

-pkcs12)
   
    openssl pkcs12 -passin pass:$5 -passout pass:$4 -in $CATOP/newcerts/$3.pem -inkey $CATOP/private/$3.key -certfile $CATOP/$CACERT -out /var/lib/tomcat7/webapps/iMovies/pkcs12/$3.p12 -export -name $2
    ;;

-revoke)

    openssl ca -revoke $2 -passin file:$CATOP/private/pass
    openssl ca -gencrl -out $CATOP/crl/iMoviesCrl.pem -passin file:$CATOP/private/pass 
    cat $CATOP/iMoviesCert.pem $CATOP/crl/iMoviesCrl.crl > $CATOP/iMoviesRevoked.pem
    RET=$?
	;;

-revcanc)

    SERIAL=`openssl x509 -in $2 -serial -noout | cut -f 2 -d =`

    openssl ca -revoke $2 -passin file:$CATOP/private/pass
    openssl ca -gencrl -out $CATOP/crl/iMoviesCrl.pem -passin file:$CATOP/private/pass
    cat $CATOP/iMoviesCert.pem $CATOP/crl/iMoviesCrl.pem > $CATOP/iMoviesRevoked.pem
    rm $2 
    rm $CATOP/private/$SERIAL.key 
    rm $CATOP/certs/$SERIAL.csr
   ;;

esac
