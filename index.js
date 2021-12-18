import React,{Component} from 'react';

import {AppRegistry,View,Text,StyleSheet} from 'react-native';


 class RNView extends Component{

    render(){
        return(
        <View style={styles.container}>
             <Text style={styles.hello}>react-native ,hello</Text>
        </View>
        )
    }

}

var styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  hello: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
});


AppRegistry.registerComponent('MyReactNativeApp', () => RNView);
