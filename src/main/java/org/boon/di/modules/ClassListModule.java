package org.boon.di.modules;

import org.boon.Exceptions;
import org.boon.Sets;
import org.boon.collections.MultiMap;
import org.boon.core.Supplier;
import org.boon.core.reflection.Reflection;
import org.boon.di.ProviderInfo;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ClassListModule extends BaseModule {

    private Map<Class, Class> classes = new ConcurrentHashMap<>();

    private MultiMap<String, Class> nameMap = new MultiMap<>();


    public ClassListModule( ProviderInfo... classes ) {

        for ( ProviderInfo info : classes ) {
            if ( info.name() != null ) {
                nameMap.put( info.name(), info.type() );
                extractClassIntoMaps( info.type(), true );
            } else {
                extractClassIntoMaps( info.type(), false );
            }
        }
    }

    public ClassListModule( Class... classes ) {

        for ( Class cls : classes ) {


            extractClassIntoMaps( cls, false );

        }
    }


    @Override
    public <T> T get( Class<T> type ) {
        try {
            return ( T ) Reflection.newInstance( classes.get( type ) );
        } catch ( Exception e ) {
            Exceptions.handle( e );
            return null;
        }
    }

    @Override
    public Object get( String name ) {
        try {
            return Reflection.newInstance( nameMap.get( name ) );
        } catch ( Exception e ) {
            Exceptions.handle( e );
            return null;
        }
    }

    @Override
    public <T> T get( Class<T> type, String name ) {

        try {
            Set<Class> set = Sets.set( nameMap.getAll( name ) );
            for ( Class<?> clazz : set ) {
                if ( type.isAssignableFrom( clazz ) ) {
                    return ( T ) Reflection.newInstance( clazz );
                }
            }

            return null;

        } catch ( Exception e ) {
            Exceptions.handle( e );
            return null;
        }

    }



    @Override
    public <T> Supplier<T> getSupplier( final Class<T> type, final String name ) {
        return new  Supplier<T>() {

            @Override
            public T get() {
                return ClassListModule.this.get(type, name);
            }
        };
    }

    @Override
    public <T> Supplier<T> getSupplier( final Class<T> type ) {
        return new  Supplier<T>() {

            @Override
            public T get() {
                return ClassListModule.this.get( type );
            }
        };
    }

    @Override
    public Iterable<Object> values() {
        return (Iterable<Object>) (Object)this.classes.values();
    }

    @Override
    public Iterable<String> names() {
        return this.nameMap.keySet();
    }

    @Override
    public Iterable types() {
        return this.classes.keySet();
    }


    @Override
    public boolean has( Class type ) {
        return classes.containsKey( type );
    }

    @Override
    public boolean has( String name ) {
        return nameMap.containsKey( name );
    }


    private void extractClassIntoMaps( Class cls, boolean foundName ) {
        this.classes.put( cls, cls );


        String named = null;

        if ( !foundName ) {

            named = NamedUtils.namedValueForClass( cls );


            if ( named != null ) {
                nameMap.put( named, cls );
                foundName = true;
            }
        }

        Class superClass = cls.getSuperclass();


        Class[] superTypes = cls.getInterfaces();

        for ( Class superType : superTypes ) {
            this.classes.put( superType, cls );
        }

        while ( superClass != Object.class ) {
            this.classes.put( superClass, cls );

            if ( !foundName ) {
                named = NamedUtils.namedValueForClass( superClass );
                if ( named != null ) {
                    nameMap.put( named, cls );
                    foundName = true;
                }
            }

            superTypes = cls.getInterfaces();
            for ( Class superType : superTypes ) {
                this.classes.put( superType, cls );
            }
            superClass = superClass.getSuperclass();
        }
    }

}
