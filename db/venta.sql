--
-- PostgreSQL database dump
--

-- Dumped from database version 10.7
-- Dumped by pg_dump version 10.7

-- Started on 2019-04-24 11:56:12

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT                       -- SELECT list can stay empty for this
      FROM   pg_catalog.pg_roles
      WHERE  rolname = 'usuario') THEN

      CREATE ROLE usuario LOGIN;
   END IF;
   ALTER USER usuario PASSWORD '1234';
END
$do$;
--
-- TOC entry 1 (class 3079 OID 12924)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2931 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- TOC entry 227 (class 1255 OID 26049)
-- Name: Articulo_HISTORICO(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."Articulo_HISTORICO"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO articuloHis(articuloId, 
articuloCodigo, articuloNombre, articuloDescripcion,
articuloPrecio, articuloUnidadesStock, articuloEstatus)
VALUES (OLD.articuloId, OLD.articuloCodigo, 
OLD.articuloNombre, OLD.articuloDescripcion, 
OLD.articuloPrecio, OLD.articuloUnidadesStock, OLD.articuloEstatus);
END;$$;


ALTER FUNCTION public."Articulo_HISTORICO"() OWNER TO postgres;

--
-- TOC entry 228 (class 1255 OID 26051)
-- Name: Articulo_LOG(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."Articulo_LOG"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO articuloLog(articuloId, articuloCodigo, articuloNombre, articuloDescripcion,
 articuloPrecio, articuloUnidadesStock, articuloEstatus, articuloLogUsuario)
VALUES (NEW.articuloId, NEW.articuloCodigo, NEW.articuloNombre, NEW.articuloDescripcion, NEW.articuloPrecio, NEW.articuloUnidadesStock, NEW.articuloEstatus, current_user);
RETURN NEW;
END;$$;


ALTER FUNCTION public."Articulo_LOG"() OWNER TO postgres;

--
-- TOC entry 233 (class 1255 OID 26123)
-- Name: Calcular_factura_TOTAL(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."Calcular_factura_TOTAL"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN

UPDATE factura
SET facturaTotalBruto = 
facturaTotalBruto + NEW.facturaPrecio * NEW.facturaCantidad,
facturaTotalImpuesto = 
facturaTotalImpuesto + NEW.facturaImpuesto * NEW.facturaCantidad,
facturaTotalDescto = 
facturaTotalDescto + NEW.facturaDescuento * NEW.facturaCantidad,
facturaTotalNeto = 
facturaTotalNeto + NEW.facturaNeto * NEW.facturaCantidad
WHERE factura.facturaId = NEW.facturaId;

UPDATE ONLY articulo
SET articuloUnidadesStock = 
articuloUnidadesStock - NEW.facturaCantidad
WHERE articuloId = NEW.articuloId;
RETURN NEW;
END;$$;


ALTER FUNCTION public."Calcular_factura_TOTAL"() OWNER TO postgres;

--
-- TOC entry 232 (class 1255 OID 26125)
-- Name: Cliente_ACCESO(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."Cliente_ACCESO"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
UPDATE Cliente
SET clienteultimatransaccion = CURRENT_TIMESTAMP
WHERE ClienteId = NEW.ClienteId; 
RETURN NEW;
END;$$;


ALTER FUNCTION public."Cliente_ACCESO"() OWNER TO postgres;

--
-- TOC entry 208 (class 1255 OID 26022)
-- Name: Cliente_HISTORICO(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."Cliente_HISTORICO"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO clienteHis(clienteId,
clienteNombre, clienteDireccion,
clienteCiudad, 
clienteEmail, 
clienteTelefono, 
clienteCodigoPostal,
clientePais, clienteEstatus)

VALUES (OLD.clienteId,
OLD.clienteNombre, OLD.clienteDireccion, OLD.clienteCiudad, OLD.clienteEmail, OLD.clienteTelefono, 
OLD.clienteCodigoPostal, OLD.clientePais, OLD.clienteEstatus);
RETURN OLD;
END;$$;


ALTER FUNCTION public."Cliente_HISTORICO"() OWNER TO postgres;

--
-- TOC entry 209 (class 1255 OID 26024)
-- Name: Cliente_LOG(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."Cliente_LOG"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN

INSERT INTO clienteLog(clienteId,
clienteNombre, clienteDireccion,
clienteCiudad, 
clienteEmail, 
clienteTelefono, 
clienteCodigoPostal,
clientePais, clienteEstatus, clienteLogUsuario)

VALUES (NEW.clienteId,
NEW.clienteNombre, NEW.clienteDireccion, NEW.clienteCiudad, NEW.clienteEmail, NEW.clienteTelefono, 
NEW.clienteCodigoPostal, NEW.clientePais, NEW.clienteEstatus, current_user);
RETURN NEW;
END;$$;


ALTER FUNCTION public."Cliente_LOG"() OWNER TO postgres;

--
-- TOC entry 210 (class 1255 OID 26026)
-- Name: Cliente_PURGAR(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."Cliente_PURGAR"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
DELETE FROM ONLY Cliente 
WHERE clienteultimatransaccion < CURRENT_TIMESTAMP - INTERVAL '5 years';
RETURN NULL;
END;$$;


ALTER FUNCTION public."Cliente_PURGAR"() OWNER TO postgres;

--
-- TOC entry 234 (class 1255 OID 26136)
-- Name: Sumar_Cargo(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."Sumar_Cargo"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
UPDATE Factura
SET FacturaTotalNeto = FacturaTotalCargo
WHERE FacturaId = NEW.FacturaId;
RETURN NEW;
END;$$;


ALTER FUNCTION public."Sumar_Cargo"() OWNER TO postgres;

--
-- TOC entry 229 (class 1255 OID 26077)
-- Name: articulo_buscar(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.articulo_buscar(nom character varying) RETURNS TABLE(id integer, codigo character, nombre character varying, descripcion character varying, unidades_stock integer, precio numeric, estatus character)
    LANGUAGE plpgsql
    AS $$BEGIN
RETURN QUERY
SELECT articuloId ID,
articuloCodigo Codigo,
articuloNombre Nombre,
articuloDescripcion Descripcion,
articuloUnidadesStock Unidades_Stock,
articuloPrecio Precio, 
articuloEstatus Estatus 
FROM ONLY articulo
WHERE articuloNombre LIKE CONCAT(nom, '%')
ORDER BY articuloid DESC;
END;$$;


ALTER FUNCTION public.articulo_buscar(nom character varying) OWNER TO postgres;

--
-- TOC entry 230 (class 1255 OID 26078)
-- Name: articulo_mostrar(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.articulo_mostrar() RETURNS TABLE(id integer, codigo character, nombre character varying, descripcion character varying, unidades_stock integer, precio numeric, estatus character)
    LANGUAGE plpgsql
    AS $$ BEGIN
RETURN QUERY
SELECT articuloId,
articuloCodigo,
articuloNombre,
articuloDescripcion,
articuloUnidadesStock,
articuloPrecio, 
articuloEstatus 
FROM ONLY articulo
ORDER BY articuloId
LIMIT 200;
END;$$;


ALTER FUNCTION public.articulo_mostrar() OWNER TO postgres;

--
-- TOC entry 231 (class 1255 OID 26079)
-- Name: articulo_mostraractivos(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.articulo_mostraractivos() RETURNS TABLE(id integer, codigo character, nombre character varying, descripcion character varying, unidades_stock integer, precio numeric, estatus character)
    LANGUAGE plpgsql
    AS $$ BEGIN
RETURN QUERY
SELECT articuloId ID,
articuloCodigo Codigo,
articuloNombre Nombre,
articuloDescripcion Descripcion,
articuloUnidadesStock Unidades_Stock,
articuloPrecio Precio, 
articuloEstatus Estatus 
FROM ONLY articulo
WHERE articuloEstatus = 'A'
ORDER BY articuloId
LIMIT 200;
END;$$;


ALTER FUNCTION public.articulo_mostraractivos() OWNER TO postgres;

--
-- TOC entry 211 (class 1255 OID 26030)
-- Name: cliente_buscar(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.cliente_buscar(nom character varying) RETURNS TABLE(id integer, nombre character varying, email character varying, telefono character varying, direccion character varying, codigopostal character varying, ciudad character varying, pais character varying, estatus character)
    LANGUAGE plpgsql
    AS $$ BEGIN
RETURN QUERY
SELECT clienteid id, 
clientenombre nombre, 
clienteemail email,
clientetelefono telefono,
clientedireccion direccion, 
clientecodigopostal codigopostal,
clienteciudad ciudad,
clientepais pais,
clienteestatus estatus
FROM ONLY cliente
WHERE clienteNombre LIKE CONCAT(nom, '%')
ORDER BY clienteid DESC
LIMIT 200;
END;$$;


ALTER FUNCTION public.cliente_buscar(nom character varying) OWNER TO postgres;

--
-- TOC entry 224 (class 1255 OID 26031)
-- Name: cliente_buscaractivos(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.cliente_buscaractivos(nom character varying) RETURNS TABLE(id integer, nombre character varying, email character varying, telefono character varying, direccion character varying, codigopostal character varying, ciudad character varying, pais character varying, estatus character)
    LANGUAGE plpgsql
    AS $$BEGIN
RETURN QUERY
SELECT clienteId AS Id, 
clienteNombre AS Nombre, 
clienteEmail AS Email, 
clienteTelefono AS Telefono,
clienteDireccion AS Direccion, 
clienteCodigoPostal AS CodigoPostal, 
clienteCiudad AS Ciudad,
clientePais AS Pais,
clienteEstatus AS Estatus
FROM ONLY cliente
WHERE clienteNombre LIKE CONCAT(nombre, '%')
AND clienteEstatus = 'A'
ORDER BY clienteultimatransaccion DESC, clienteid;
END;$$;


ALTER FUNCTION public.cliente_buscaractivos(nom character varying) OWNER TO postgres;

--
-- TOC entry 225 (class 1255 OID 26032)
-- Name: cliente_mostrar(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.cliente_mostrar() RETURNS TABLE(id integer, nombre character varying, email character varying, telefono character varying, direccion character varying, codigopostal character varying, ciudad character varying, pais character varying, estatus character)
    LANGUAGE plpgsql
    AS $$ BEGIN
RETURN QUERY
SELECT clienteid, 
clientenombre, 
clienteemail,
clientetelefono,
clientedireccion, 
clientecodigopostal,
clienteciudad,
clientepais,
clienteestatus
FROM ONLY cliente
ORDER BY clienteid
LIMIT 200;
END;$$;


ALTER FUNCTION public.cliente_mostrar() OWNER TO postgres;

--
-- TOC entry 226 (class 1255 OID 26033)
-- Name: cliente_mostraractivos(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.cliente_mostraractivos() RETURNS TABLE(id integer, nombre character varying, email character varying, telefono character varying, direccion character varying, codigopostal character varying, ciudad character varying, pais character varying, estatus character)
    LANGUAGE plpgsql
    AS $$ BEGIN
RETURN QUERY
SELECT clienteid, 
clientenombre, 
clienteemail,
clientetelefono,
clientedireccion, 
clientecodigopostal,
clienteciudad,
clientepais,
clienteestatus
FROM ONLY cliente
WHERE clienteEstatus = 'A'
ORDER BY clienteultimatransaccion DESC, clienteid
LIMIT 200;
END;$$;


ALTER FUNCTION public.cliente_mostraractivos() OWNER TO postgres;

--
-- TOC entry 236 (class 1255 OID 26133)
-- Name: factura_mostrar(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.factura_mostrar() RETURNS TABLE(no integer, nombre character varying, id integer, numero integer, fecha date, hora time without time zone, total_bruto numeric, total_descuento numeric, total_impuesto numeric, total_cargo numeric, total_neto numeric)
    LANGUAGE plpgsql
    AS $$BEGIN
RETURN QUERY
SELECT s.clienteId N, 
s.clienteNombre Nombre, 
facturaId Id, facturaNumero Numero,  
cast(facturaFecha AS DATE) AS Fecha, cast(facturaFecha AS TIME) AS Hora,
facturaTotalBruto AS Total_Bruto, 
facturaTotalDescto AS Total_Descuento, 
facturaTotalImpuesto AS Total_Impuesto,
facturaTotalCargo AS Total_Cargo, 
facturaTotalNeto AS Total_Neto
FROM factura o 
INNER JOIN ONLY cliente s
ON o.clienteId = s.clienteId
WHERE facturaEstatus = 'P'
ORDER BY facturaId DESC
LIMIT 200;
END;$$;


ALTER FUNCTION public.factura_mostrar() OWNER TO postgres;

--
-- TOC entry 237 (class 1255 OID 26129)
-- Name: factura_mostrar(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.factura_mostrar(numero integer) RETURNS TABLE(linea smallint, codigo_articulo character, articulo character varying, descripcion character varying, cantidad integer, precio numeric, descuento numeric, impuesto numeric, neto numeric)
    LANGUAGE plpgsql
    AS $$ BEGIN
RETURN QUERY
SELECT  facturaLinea AS Linea, 
articuloCodigo AS Codigo_articulo, 
articuloNombre AS articulo, 
articuloDescripcion AS Descripcion, 
facturaCantidad AS Cantidad,
facturaPrecio AS Precio,
facturaDescuento AS Descuento, 
facturaImpuesto AS Impuesto, 
facturaNeto AS Neto
FROM facturaDetalle d
INNER JOIN ONLY articulo p
ON d.articuloId = p.articuloId
INNER JOIN factura o
ON o.facturaId = d.facturaId
WHERE o.facturaNumero = numero
ORDER BY facturaLinea ASC
LIMIT 15;
END;$$;


ALTER FUNCTION public.factura_mostrar(numero integer) OWNER TO postgres;

--
-- TOC entry 235 (class 1255 OID 26132)
-- Name: facturadetalle_insertar(integer, integer, numeric, integer, numeric, numeric); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.facturadetalle_insertar(id integer, articuloid integer, precio numeric, cantidad integer, descuento numeric, impuesto numeric) RETURNS void
    LANGUAGE plpgsql
    AS $$ 
DECLARE rprecio NUMERIC;  
BEGIN
SELECT articuloprecio INTO rprecio FROM articulo;
INSERT INTO facturadetalle(facturaid, articuloid,
    facturaPrecio, facturaCantidad, facturaDescuento, 
    facturaImpuesto, facturaNeto)
    VALUES
    (
		id, articuloid, rprecio, cantidad, descuento, impuesto , 
        rprecio + impuesto - descuento);
END;   $$;


ALTER FUNCTION public.facturadetalle_insertar(id integer, articuloid integer, precio numeric, cantidad integer, descuento numeric, impuesto numeric) OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 26028)
-- Name: articulo_articuloid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.articulo_articuloid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.articulo_articuloid_seq OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 201 (class 1259 OID 26034)
-- Name: articulo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.articulo (
    articuloid integer DEFAULT nextval('public.articulo_articuloid_seq'::regclass) NOT NULL,
    articulocodigo character(24),
    articulonombre character varying(40) NOT NULL,
    articulodescripcion character varying(512),
    articulounidadesstock integer,
    articuloprecio numeric(10,2),
    articuloestatus character(1) DEFAULT 'A'::bpchar,
    CONSTRAINT articulo_articulonombre_check CHECK (((articulonombre)::text <> ''::text)),
    CONSTRAINT natural_unidadesstock CHECK ((articulounidadesstock >= 0)),
    CONSTRAINT positivo_precio CHECK ((articuloprecio > (0)::numeric))
);


ALTER TABLE public.articulo OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 26065)
-- Name: articulohis; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.articulohis (
    articulohisfecha timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0)
)
INHERITS (public.articulo);


ALTER TABLE public.articulohis OWNER TO postgres;

--
-- TOC entry 2952 (class 0 OID 0)
-- Dependencies: 203
-- Name: TABLE articulohis; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.articulohis IS 'Registra las purgaciones hechas en la tabla articulo.';


--
-- TOC entry 202 (class 1259 OID 26053)
-- Name: articulolog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.articulolog (
    articulologusuario character varying(45),
    articulologfecha timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0)
)
INHERITS (public.articulo);


ALTER TABLE public.articulolog OWNER TO postgres;

--
-- TOC entry 2954 (class 0 OID 0)
-- Dependencies: 202
-- Name: TABLE articulolog; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.articulolog IS 'Registra las inserciones hechas en la tabla articulo.';


--
-- TOC entry 196 (class 1259 OID 25980)
-- Name: cliente_clienteid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.cliente_clienteid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.cliente_clienteid_seq OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 25982)
-- Name: cliente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cliente (
    clienteid integer DEFAULT nextval('public.cliente_clienteid_seq'::regclass) NOT NULL,
    clientenombre character varying(100) NOT NULL,
    clienteemail character varying(254),
    clientetelefono character varying(15),
    clientedireccion character varying(95),
    clientecodigopostal character varying(10),
    clienteciudad character varying(35),
    clientepais character varying(70),
    clienteultimatransaccion timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0),
    clienteestatus character(1) DEFAULT 'A'::bpchar,
    CONSTRAINT cliente_clientenombre_check CHECK (((clientenombre)::text <> ''::text))
);


ALTER TABLE public.cliente OWNER TO postgres;

--
-- TOC entry 198 (class 1259 OID 26000)
-- Name: clientehis; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.clientehis (
    clientehisfecha timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0)
)
INHERITS (public.cliente);


ALTER TABLE public.clientehis OWNER TO postgres;

--
-- TOC entry 2958 (class 0 OID 0)
-- Dependencies: 198
-- Name: TABLE clientehis; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.clientehis IS 'Registra las purgaciones hechas en la tabla cliente.';


--
-- TOC entry 199 (class 1259 OID 26011)
-- Name: clientelog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.clientelog (
    clientelogusuario character varying(45),
    clientelogfecha timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0)
)
INHERITS (public.cliente);


ALTER TABLE public.clientelog OWNER TO postgres;

--
-- TOC entry 2960 (class 0 OID 0)
-- Dependencies: 199
-- Name: TABLE clientelog; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.clientelog IS 'Registra las inserciones hechas en la tabla cliente.';


--
-- TOC entry 204 (class 1259 OID 26080)
-- Name: factura_facturaid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.factura_facturaid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.factura_facturaid_seq OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 26082)
-- Name: factura; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.factura (
    facturaid integer DEFAULT nextval('public.factura_facturaid_seq'::regclass) NOT NULL,
    facturanumero integer DEFAULT currval('public.factura_facturaid_seq'::regclass) NOT NULL,
    clienteid integer,
    facturafecha timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0) NOT NULL,
    facturaestatus character(1) DEFAULT 'P'::bpchar,
    facturatotalbruto numeric(12,2) DEFAULT 0.00,
    facturatotaldescto numeric(8,2) DEFAULT 0.00,
    facturatotalimpuesto numeric(8,2) DEFAULT 0.00,
    facturatotalcargo numeric(8,2) DEFAULT 200.00,
    facturatotalneto numeric(12,2) DEFAULT 0.00
);


ALTER TABLE public.factura OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 26101)
-- Name: facturadetalle_facturalinea_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.facturadetalle_facturalinea_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.facturadetalle_facturalinea_seq OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 26103)
-- Name: facturadetalle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.facturadetalle (
    facturalinea smallint DEFAULT nextval('public.facturadetalle_facturalinea_seq'::regclass) NOT NULL,
    facturaid integer NOT NULL,
    articuloid integer,
    facturacantidad integer,
    facturaprecio numeric(10,2),
    facturadescuento numeric(8,2) DEFAULT 0.00,
    facturaimpuesto numeric(8,2) DEFAULT 0.00,
    facturaneto numeric(10,2) DEFAULT 0.00,
    CONSTRAINT natural_cantidad CHECK ((facturacantidad > 0))
);


ALTER TABLE public.facturadetalle OWNER TO postgres;

--
-- TOC entry 2756 (class 2604 OID 26068)
-- Name: articulohis articuloid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articulohis ALTER COLUMN articuloid SET DEFAULT nextval('public.articulo_articuloid_seq'::regclass);


--
-- TOC entry 2757 (class 2604 OID 26069)
-- Name: articulohis articuloestatus; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articulohis ALTER COLUMN articuloestatus SET DEFAULT 'A'::bpchar;


--
-- TOC entry 2750 (class 2604 OID 26056)
-- Name: articulolog articuloid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articulolog ALTER COLUMN articuloid SET DEFAULT nextval('public.articulo_articuloid_seq'::regclass);


--
-- TOC entry 2751 (class 2604 OID 26057)
-- Name: articulolog articuloestatus; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articulolog ALTER COLUMN articuloestatus SET DEFAULT 'A'::bpchar;


--
-- TOC entry 2735 (class 2604 OID 26003)
-- Name: clientehis clienteid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientehis ALTER COLUMN clienteid SET DEFAULT nextval('public.cliente_clienteid_seq'::regclass);


--
-- TOC entry 2736 (class 2604 OID 26004)
-- Name: clientehis clienteultimatransaccion; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientehis ALTER COLUMN clienteultimatransaccion SET DEFAULT CURRENT_TIMESTAMP(0);


--
-- TOC entry 2737 (class 2604 OID 26005)
-- Name: clientehis clienteestatus; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientehis ALTER COLUMN clienteestatus SET DEFAULT 'A'::bpchar;


--
-- TOC entry 2740 (class 2604 OID 26014)
-- Name: clientelog clienteid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientelog ALTER COLUMN clienteid SET DEFAULT nextval('public.cliente_clienteid_seq'::regclass);


--
-- TOC entry 2741 (class 2604 OID 26015)
-- Name: clientelog clienteultimatransaccion; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientelog ALTER COLUMN clienteultimatransaccion SET DEFAULT CURRENT_TIMESTAMP(0);


--
-- TOC entry 2742 (class 2604 OID 26016)
-- Name: clientelog clienteestatus; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientelog ALTER COLUMN clienteestatus SET DEFAULT 'A'::bpchar;


--
-- TOC entry 2785 (class 2606 OID 26046)
-- Name: articulo pk_articulo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articulo
    ADD CONSTRAINT pk_articulo PRIMARY KEY (articuloid);


--
-- TOC entry 2777 (class 2606 OID 25993)
-- Name: cliente pk_cliente; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT pk_cliente PRIMARY KEY (clienteid);


--
-- TOC entry 2789 (class 2606 OID 26095)
-- Name: factura pk_factura; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.factura
    ADD CONSTRAINT pk_factura PRIMARY KEY (facturaid);


--
-- TOC entry 2791 (class 2606 OID 26112)
-- Name: facturadetalle pk_facturadetalle; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.facturadetalle
    ADD CONSTRAINT pk_facturadetalle PRIMARY KEY (facturalinea, facturaid);


--
-- TOC entry 2787 (class 2606 OID 26048)
-- Name: articulo un_articulocodigo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articulo
    ADD CONSTRAINT un_articulocodigo UNIQUE (articulocodigo);


--
-- TOC entry 2779 (class 2606 OID 25995)
-- Name: cliente un_clientedireccion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT un_clientedireccion UNIQUE (clientedireccion);


--
-- TOC entry 2781 (class 2606 OID 25997)
-- Name: cliente un_clienteemail; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT un_clienteemail UNIQUE (clienteemail);


--
-- TOC entry 2783 (class 2606 OID 25999)
-- Name: cliente un_clientetelefono; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT un_clientetelefono UNIQUE (clientetelefono);


--
-- TOC entry 2802 (class 2620 OID 26124)
-- Name: facturadetalle Factura_TOTAL; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER "Factura_TOTAL" AFTER INSERT ON public.facturadetalle FOR EACH ROW EXECUTE PROCEDURE public."Calcular_factura_TOTAL"();


--
-- TOC entry 2795 (class 2620 OID 26023)
-- Name: cliente HISTORICO; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER "HISTORICO" AFTER DELETE ON public.cliente FOR EACH ROW EXECUTE PROCEDURE public."Cliente_HISTORICO"();


--
-- TOC entry 2798 (class 2620 OID 26050)
-- Name: articulo HISTORICO; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER "HISTORICO" AFTER DELETE ON public.articulo FOR EACH ROW EXECUTE PROCEDURE public."Articulo_HISTORICO"();


--
-- TOC entry 2796 (class 2620 OID 26025)
-- Name: cliente LOG; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER "LOG" AFTER INSERT OR UPDATE ON public.cliente FOR EACH ROW EXECUTE PROCEDURE public."Cliente_LOG"();


--
-- TOC entry 2799 (class 2620 OID 26052)
-- Name: articulo LOG; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER "LOG" AFTER INSERT OR UPDATE ON public.articulo FOR EACH ROW EXECUTE PROCEDURE public."Articulo_LOG"();


--
-- TOC entry 2797 (class 2620 OID 26027)
-- Name: cliente PURGACION; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER "PURGACION" AFTER INSERT OR UPDATE ON public.cliente FOR EACH ROW EXECUTE PROCEDURE public."Cliente_PURGAR"();


--
-- TOC entry 2800 (class 2620 OID 26137)
-- Name: factura SUMACARGO; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER "SUMACARGO" AFTER INSERT ON public.factura FOR EACH ROW EXECUTE PROCEDURE public."Sumar_Cargo"();


--
-- TOC entry 2801 (class 2620 OID 26126)
-- Name: factura ULTIMO_ACCESO; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER "ULTIMO_ACCESO" AFTER INSERT ON public.factura FOR EACH ROW EXECUTE PROCEDURE public."Cliente_ACCESO"();


--
-- TOC entry 2792 (class 2606 OID 26096)
-- Name: factura FK_clienteId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.factura
    ADD CONSTRAINT "FK_clienteId" FOREIGN KEY (clienteid) REFERENCES public.cliente(clienteid);


--
-- TOC entry 2794 (class 2606 OID 26118)
-- Name: facturadetalle fk_articuloid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.facturadetalle
    ADD CONSTRAINT fk_articuloid FOREIGN KEY (articuloid) REFERENCES public.articulo(articuloid);


--
-- TOC entry 2793 (class 2606 OID 26113)
-- Name: facturadetalle fk_facturaid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.facturadetalle
    ADD CONSTRAINT fk_facturaid FOREIGN KEY (facturaid) REFERENCES public.factura(facturaid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2932 (class 0 OID 0)
-- Dependencies: 227
-- Name: FUNCTION "Articulo_HISTORICO"(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public."Articulo_HISTORICO"() TO usuario;


--
-- TOC entry 2933 (class 0 OID 0)
-- Dependencies: 228
-- Name: FUNCTION "Articulo_LOG"(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public."Articulo_LOG"() TO usuario;


--
-- TOC entry 2934 (class 0 OID 0)
-- Dependencies: 233
-- Name: FUNCTION "Calcular_factura_TOTAL"(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public."Calcular_factura_TOTAL"() TO usuario;


--
-- TOC entry 2935 (class 0 OID 0)
-- Dependencies: 232
-- Name: FUNCTION "Cliente_ACCESO"(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public."Cliente_ACCESO"() TO usuario;


--
-- TOC entry 2936 (class 0 OID 0)
-- Dependencies: 208
-- Name: FUNCTION "Cliente_HISTORICO"(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public."Cliente_HISTORICO"() TO usuario;


--
-- TOC entry 2937 (class 0 OID 0)
-- Dependencies: 209
-- Name: FUNCTION "Cliente_LOG"(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public."Cliente_LOG"() TO usuario;


--
-- TOC entry 2938 (class 0 OID 0)
-- Dependencies: 210
-- Name: FUNCTION "Cliente_PURGAR"(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public."Cliente_PURGAR"() TO usuario;


--
-- TOC entry 2939 (class 0 OID 0)
-- Dependencies: 234
-- Name: FUNCTION "Sumar_Cargo"(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public."Sumar_Cargo"() TO usuario;


--
-- TOC entry 2940 (class 0 OID 0)
-- Dependencies: 229
-- Name: FUNCTION articulo_buscar(nom character varying); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.articulo_buscar(nom character varying) TO usuario;


--
-- TOC entry 2941 (class 0 OID 0)
-- Dependencies: 230
-- Name: FUNCTION articulo_mostrar(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.articulo_mostrar() TO usuario;


--
-- TOC entry 2942 (class 0 OID 0)
-- Dependencies: 231
-- Name: FUNCTION articulo_mostraractivos(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.articulo_mostraractivos() TO usuario;


--
-- TOC entry 2943 (class 0 OID 0)
-- Dependencies: 211
-- Name: FUNCTION cliente_buscar(nom character varying); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.cliente_buscar(nom character varying) TO usuario;


--
-- TOC entry 2944 (class 0 OID 0)
-- Dependencies: 224
-- Name: FUNCTION cliente_buscaractivos(nom character varying); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.cliente_buscaractivos(nom character varying) TO usuario;


--
-- TOC entry 2945 (class 0 OID 0)
-- Dependencies: 225
-- Name: FUNCTION cliente_mostrar(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.cliente_mostrar() TO usuario;


--
-- TOC entry 2946 (class 0 OID 0)
-- Dependencies: 226
-- Name: FUNCTION cliente_mostraractivos(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.cliente_mostraractivos() TO usuario;


--
-- TOC entry 2947 (class 0 OID 0)
-- Dependencies: 236
-- Name: FUNCTION factura_mostrar(); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.factura_mostrar() TO usuario;


--
-- TOC entry 2948 (class 0 OID 0)
-- Dependencies: 237
-- Name: FUNCTION factura_mostrar(numero integer); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.factura_mostrar(numero integer) TO usuario;


--
-- TOC entry 2949 (class 0 OID 0)
-- Dependencies: 235
-- Name: FUNCTION facturadetalle_insertar(id integer, articuloid integer, precio numeric, cantidad integer, descuento numeric, impuesto numeric); Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON FUNCTION public.facturadetalle_insertar(id integer, articuloid integer, precio numeric, cantidad integer, descuento numeric, impuesto numeric) TO usuario;


--
-- TOC entry 2950 (class 0 OID 0)
-- Dependencies: 200
-- Name: SEQUENCE articulo_articuloid_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.articulo_articuloid_seq TO usuario;


--
-- TOC entry 2951 (class 0 OID 0)
-- Dependencies: 201
-- Name: TABLE articulo; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.articulo TO usuario;


--
-- TOC entry 2953 (class 0 OID 0)
-- Dependencies: 203
-- Name: TABLE articulohis; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.articulohis TO usuario;


--
-- TOC entry 2955 (class 0 OID 0)
-- Dependencies: 202
-- Name: TABLE articulolog; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.articulolog TO usuario;


--
-- TOC entry 2956 (class 0 OID 0)
-- Dependencies: 196
-- Name: SEQUENCE cliente_clienteid_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.cliente_clienteid_seq TO usuario;


--
-- TOC entry 2957 (class 0 OID 0)
-- Dependencies: 197
-- Name: TABLE cliente; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.cliente TO usuario;


--
-- TOC entry 2959 (class 0 OID 0)
-- Dependencies: 198
-- Name: TABLE clientehis; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.clientehis TO usuario;


--
-- TOC entry 2961 (class 0 OID 0)
-- Dependencies: 199
-- Name: TABLE clientelog; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.clientelog TO usuario;


--
-- TOC entry 2962 (class 0 OID 0)
-- Dependencies: 204
-- Name: SEQUENCE factura_facturaid_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.factura_facturaid_seq TO usuario;


--
-- TOC entry 2963 (class 0 OID 0)
-- Dependencies: 205
-- Name: TABLE factura; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.factura TO usuario;


--
-- TOC entry 2964 (class 0 OID 0)
-- Dependencies: 206
-- Name: SEQUENCE facturadetalle_facturalinea_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.facturadetalle_facturalinea_seq TO usuario;


--
-- TOC entry 2965 (class 0 OID 0)
-- Dependencies: 207
-- Name: TABLE facturadetalle; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.facturadetalle TO usuario;


--
-- TOC entry 1728 (class 826 OID 25978)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: -; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT SELECT,USAGE ON SEQUENCES  TO usuario;


--
-- TOC entry 1729 (class 826 OID 25979)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: -; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON FUNCTIONS  TO usuario;


--
-- TOC entry 1727 (class 826 OID 25977)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: -; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES  TO usuario;


-- Completed on 2019-04-24 11:56:12

--
-- PostgreSQL database dump complete
--

