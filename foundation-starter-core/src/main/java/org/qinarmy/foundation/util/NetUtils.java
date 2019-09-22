package org.qinarmy.foundation.util;


import org.qinarmy.sys.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;


/**
 * 用于网络 的工具,如 ip 等
 * created  on 2018/10/18.
 */
public abstract class NetUtils {

    private static final Logger LOG = LoggerFactory.getLogger( NetUtils.class );

    private static final Map<OS, String[]> OS_INTERFACE;

    private static final String[] DEFAULT_INTERFACE_NAME = {"eth0"};

    static {
        Map<OS, String[]> map = new HashMap<>();
        map.put( OS.LINUX, DEFAULT_INTERFACE_NAME );
        map.put( OS.MAC, new String[]{"en0"} );
        map.put( OS.WINDOWS, DEFAULT_INTERFACE_NAME );
        // 由于作者没有 solaris 系统,不知默认 interface 名故待以后优化.

        OS_INTERFACE = Collections.unmodifiableMap( map );
    }

    /**
     * ipv4 私有地址的类型
     */
    public enum PrivateType {
        A( new String[]{"10."} ),//10.0.0.0 ~ 10.255.255.255
       // B(new String[]{ "172.16.","172.31."} ),// 172.16.0.0 ~ 172.31.255.255
        C( new String[]{"192.168."} ) // 192.168.0.0 ~ 192.168.255.255
        ;
        private final String[] start;

        PrivateType(String[] start) {
            this.start = start;
        }

        public String[] start() {
            return start;
        }

        public boolean isMatch(String ipv4Address) {
            boolean match = false;
            for (String s : start) {
                if(ipv4Address != null && ipv4Address.startsWith( s )){
                    match  = true;
                    break;
                }
            }
          return match;
        }
    }


    public static boolean isIpV4(String hostName) {
        boolean yes;
        try {
            InetAddress address = InetAddress.getByName( hostName );
            yes = address instanceof Inet4Address;
        } catch (Exception e) {
            yes = false;
        }
        return yes;
    }

    public static boolean isIpV6(String hostName) {
        boolean yes;
        try {
            InetAddress address = InetAddress.getByName( hostName );
            yes = address instanceof Inet6Address;
        } catch (Exception e) {
            yes = false;
        }
        return yes;
    }


    public static boolean isPublic(String hostName) {
        boolean yes;
        try {
            InetAddress address = InetAddress.getByName( hostName );
            yes = !address.isSiteLocalAddress();
        } catch (UnknownHostException e) {
            yes = false;
        }
        return yes;
    }

    public static boolean isPrivate(String hostName) {
        boolean yes;
        try {
            InetAddress address = InetAddress.getByName( hostName );
            yes = address.isSiteLocalAddress();
        } catch (UnknownHostException e) {
            yes = false;
        }
        return yes;
    }



    /**
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param timeout 每个 interface 的超时时间,毫秒
     * @return ip 是一个有效地址且可到达.
     * @see #isReachable(InetAddress, int)
     */
    public static boolean isReachable(String ip, int timeout) {
        boolean yes;
        try {
            yes = isReachable( InetAddress.getByName( ip ), timeout );
        } catch (UnknownHostException e) {
            yes = false;
        }
        return yes;
    }


    /**
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param timeout 每个 interface 的超时时间,毫秒
     * @see #isReachable(InetAddress, int, int)
     */
    public static boolean isReachable(InetAddress address, int timeout) {
        return isReachable( address, 0, timeout );
    }

    /**
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param address not null
     * @param ttl     尝试次数
     * @param timeout 每个 interface 的超时时间,毫秒
     * @return true 可到达
     * @see InetAddress#isReachable(NetworkInterface, int, int)
     */
    public static boolean isReachable(InetAddress address, int ttl, int timeout) {
        boolean yes = false;
        try {
            yes =  address.isReachable( null, ttl, timeout );
        } catch (IOException  e) {
            // io 获取失败,不可到达.
        }
        return yes;
    }


    /**
     * 等价于 {@link #getRoutSourceAddress(String hostName, int timeout)}
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param timeout  每个 interface 的超时时间,毫秒
     * @param hostName not null,主机名(可为 ip)
     * @return null or ip
     * @see #getRoutSourceAddress(InetAddress, int, int, Class)
     */
    public static String getRoutSourceAddressAsString(String hostName, int timeout) {
        InetAddress address;
        address = getRoutSourceAddress( hostName, timeout );
        return address == null ? null : address.getHostAddress();

    }


    /**
     * 等价于 {@link #getRoutSourceAddress(InetAddress target, int timeout)}
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param timeout  每个 interface 的超时时间,毫秒
     * @param hostName not null,主机名(可为 ip)
     * @return null or InetAddress
     * @see #getRoutSourceAddress(InetAddress, int, int, Class)
     */
    public static InetAddress getRoutSourceAddress(String hostName, int timeout) {
        InetAddress address;
        try {
            address = getRoutSourceAddress( InetAddress.getByName( hostName ), 0, timeout, Inet4Address.class );
        } catch (UnknownHostException e) {
            address = null;
        }
        return address;
    }


    /**
     * 等价于 {@link #getRoutSourceAddress(InetAddress target, int 0, int timeout, Class Inet4Address)}
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param timeout 每个 interface 的超时时间,毫秒
     * @return null or InetAddress
     * @see #getRoutSourceAddress(InetAddress, int, int, Class)
     */
    public static InetAddress getRoutSourceAddress(InetAddress target, int timeout) {
        return getRoutSourceAddress( target, 0, timeout, Inet4Address.class );
    }


    /**
     * 获取能到达 address 相应 {@link NetworkInterface} 的第一个地址.
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param target  要到达的地址, not null
     * @param ttl     尝试次数
     * @param timeout 每个 interface 的超时时间,毫秒
     * @param clazz   地址版本 nullable,若 not null,则优先返回此版本
     * @return null or address
     * @see InetAddress#isReachable(NetworkInterface, int, int)
     */
    public static InetAddress getRoutSourceAddress(InetAddress target, int ttl, int timeout
            , Class<? extends InetAddress> clazz) {
        NetworkInterface nf = getRoutInterface( target, ttl, timeout );
        InetAddress source = null;
        if (nf != null) {
            Enumeration<InetAddress> addrs = nf.getInetAddresses();
            InetAddress first = null;
            while (addrs.hasMoreElements()) {
                source = addrs.nextElement();
                if (first == null) {
                    first = source;
                }
                if (clazz == null || clazz.isInstance( source )) {
                    return source;
                }
            }
            //没有找到指定版本的,返回 第一个.
            source = first;
        }

        return source;
    }


    /**
     * 等价于 {@link #getRoutInterface(InetAddress address, int 0, int timeout)}
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param hostName not null,主机名,可是 ip
     * @param timeout  每个 interface 的超时时间,毫秒
     * @return null or {@link NetworkInterface}
     */
    public static NetworkInterface getRoutInterface(String hostName, int timeout) {
        NetworkInterface nf;
        try {
            nf = getRoutInterface( InetAddress.getByName( hostName ), 0, timeout );
        } catch (UnknownHostException e) {
            nf = null;
        }
        return nf;
    }

    /**
     * 等价于 {@link #getRoutInterface(InetAddress address, int 0, int timeout)}
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param address not null
     * @param timeout 每个 interface 的超时时间,毫秒
     * @return null or {@link NetworkInterface}
     */
    public static NetworkInterface getRoutInterface(InetAddress address, int timeout) {
        return getRoutInterface( address, 0, timeout );
    }

    /**
     * 获取 能到达 address 的 {@link NetworkInterface}
     * <strong>由于人有网络操作,此方法耗时</strong>
     *
     * @param address not null
     * @param timeout 每个 interface 的超时时间,毫秒
     * @return null or {@link NetworkInterface}
     */
    public static NetworkInterface getRoutInterface(final InetAddress address, int ttl, int timeout) {
        NetworkInterface nf = null;

        boolean yes = false;
        try {
            Enumeration<NetworkInterface> nfs = NetworkInterface.getNetworkInterfaces();
            while (nfs.hasMoreElements()) {
                nf = nfs.nextElement();
                try {
                    yes = address.isReachable( nf, ttl, timeout );
                    if (yes) {
                        break;
                    }
                } catch (IOException e) {
                    //忽略,此 interface 不可到达.
                }

            }
        } catch (SocketException  e) {
            // interface 获取失败,未知主机,不可到达.
        }
        return yes ? nf : null;
    }


    /**
     * 获取能查询到的第一个私有地址, 这个地址可以是 ipv4 与可以是 ipv6
     * 使用 {@link InetAddress#getHostAddress()} 获取字符串表示
     *
     * @return not null
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    public static InetAddress getFirstPrivateAddr() throws RuntimeException {
        return getPrivateAddrPrecedenceDefault( null, null );
    }

    /**
     * 获取能查询到的第一个私有地址, 这个地址可以是 ipv4 与可以是 ipv6
     * 使用 {@link InetAddress#getHostAddress()} 获取字符串表示
     *
     * @return not null
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    public static String getFirstPrivateAddrAsString() throws RuntimeException {
        return getFirstPrivateAddr().getHostAddress();
    }


    /**
     * 获取一个私有地址,优先 v4
     * 使用 {@link InetAddress#getHostAddress()} 获取字符串表示
     *
     * @return not null
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    public static InetAddress getPrivateAddr() throws RuntimeException {
        return getPrivateAddrPrecedenceDefault( Inet4Address.class, null );
    }

    /**
     * 获取一个私有地址,优先 v6
     * 使用 {@link InetAddress#getHostAddress()} 获取字符串表示
     *
     * @return not null
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    public static InetAddress getPrivateAddr(boolean precedenceV6) throws RuntimeException {
        return getPrivateAddrPrecedenceDefault(precedenceV6 ?  Inet6Address.class : Inet4Address.class, null );
    }

    /**
     * 获取能查询到的第一个 v4 私有地址.
     * 使用 {@link InetAddress#getHostAddress()} 获取字符串表示
     *
     * @return null or InetAddress
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    public static InetAddress getFirstAddr() throws RuntimeException {
        InetAddress address = getPrivateAddrPrecedenceDefault( Inet4Address.class, null );
        if (address instanceof Inet6Address) {
            address = null;
        }
        return address;
    }

    /**
     * 获取能查询到的第一个 v6 私有地址.
     * 使用 {@link InetAddress#getHostAddress()} 获取字符串表示
     *
     * @return null or InetAddress
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    public static InetAddress getFirstV6PrivateAddr() throws RuntimeException {
        InetAddress address = getPrivateAddrPrecedenceDefault( Inet6Address.class, null );
        if (address instanceof Inet4Address) {
            address = null;
        }
        return address;
    }

    /**
     * 获取私有地址,优先使用 默认的 interface ,如 : eth0,en0
     *
     * @param precedenceVersion nullable,若不为 null 则优先返回指定版本
     * @param precedenceType    nullable ,若不为 null 则优先返回指定的版本
     * @return not null
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    public static InetAddress getPrivateAddrPrecedenceDefault(Class<? extends InetAddress> precedenceVersion
            , PrivateType precedenceType) throws RuntimeException {

        List<NetworkInterface> interfaceList = new ArrayList<>( 2 );
        for (String name : getDefaultInterFaceNames()) {
            try {
                interfaceList.add( NetworkInterface.getByName( name ) );
            } catch (SocketException e) {
                // 忽略
            }

        }

        final Iterator<NetworkInterface> iterator = interfaceList.iterator();
        Enumeration<NetworkInterface> nfs = new Enumeration<NetworkInterface>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public NetworkInterface nextElement() {
                return iterator.next();
            }
        };

        InetAddress address;
        address = doGetPrivateAddr( nfs, precedenceVersion, precedenceType );
        if (address == null) {
            // 从其它 interface 获取 私有地址
            address = getPrivateAddr( precedenceVersion, precedenceType );
            if (address == null) {
                throwNotFoundPrivateAddressException();
            }
        }
        return address;

    }

    /**
     * 获取能查询 指定版本 私有地址.
     * 使用 {@link InetAddress#getHostAddress()} 获取字符串表示
     *
     * @param precedenceVersion nullable,若不为 null 则优先返回指定版本
     * @param precedenceType    nullable ,若不为 null 则优先返回指定的版本
     * @return not null
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    public static InetAddress getPrivateAddr(Class<? extends InetAddress> precedenceVersion
            , PrivateType precedenceType) throws RuntimeException {
        try {
            return doGetPrivateAddr( NetworkInterface.getNetworkInterfaces(), precedenceVersion, precedenceType );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException( e.getMessage(), e );
        }

    }

    /**
     * 获取能查询 指定版本 私有地址.
     * 使用 {@link InetAddress#getHostAddress()} 获取字符串表示
     *
     * @param nfs               not null
     * @param precedenceVersion nullable,若不为 null 则优先返回指定版本
     * @param precedenceType    nullable ,若不为 null 则优先返回指定的版本
     * @return null or address
     * @throws RuntimeException io 出错,若业务上对此异常有强要求,请捕捉,即不强制捕捉,因为这个异常通常不抛出.
     */
    private static InetAddress doGetPrivateAddr(Enumeration<NetworkInterface> nfs
            , Class<? extends InetAddress> precedenceVersion
            , PrivateType precedenceType) throws RuntimeException {
        NetworkInterface nf;
        Enumeration<InetAddress> addrs;
        InetAddress addr;
        List<InetAddress> addressList = new ArrayList<>( 6 );

        while (nfs.hasMoreElements()) {
            nf = nfs.nextElement();
            if (nf == null) {
                continue;
            }
            addrs = nf.getInetAddresses();
            while (addrs.hasMoreElements()) {
                addr = addrs.nextElement();
                if (!addr.isSiteLocalAddress()) {
                    continue;
                }

                if (precedenceVersion == null) {
                    // 没有版本指定,返回
                    return addr;
                }
                if (Inet6Address.class == precedenceVersion) {
                    if (addr instanceof Inet6Address) {
                        return addr;
                    }
                } else if (Inet4Address.class == precedenceVersion) {
                    if (precedenceType == null || precedenceType.isMatch( addr.getHostAddress() )) {
                        return addr;
                    }
                }
                addressList.add( addr );
            }
        }
        if (addressList.isEmpty()) {
            addr = null;
        } else {
            addr = addressList.get( 0 );
            if (precedenceType == null) {
                return addr;
            }
            for (InetAddress address : addressList) {
                if (precedenceType.isMatch( address.getHostAddress() )) {
                    addr = address;
                }
            }
        }
        return addr;
    }


    private static void throwNotFoundPrivateAddressException() throws RuntimeException {
        throw new RuntimeException( "os not found private address. io error" );
    }


    private static String[] getDefaultInterFaceNames() {
        return OS_INTERFACE.getOrDefault( OS.localOs(), DEFAULT_INTERFACE_NAME );
    }



}
