cmake_minimum_required(VERSION 3.4.1)

#查找系统库 liblog.so
find_library( log-lib
              log )

find_library( android-lib
              android )


#变量
set(distribution_DIR ../../../../libs)


add_library( jpegbither
             SHARED
             IMPORTED)

set_target_properties( jpegbither
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${ANDROID_ABI}/libjpegbither.so)


#C++11标准
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_library( compress_utils
             SHARED
             src/main/cpp/compress_utils.cpp)

target_link_libraries(compress_utils log android -ljnigraphics)


#预编译库中引用文件夹中的提示
include_directories(src/main/cpp/jpeg)

target_link_libraries( compress_utils
                       jpegbither
                       ${android-lib}
                       ${log-lib} )