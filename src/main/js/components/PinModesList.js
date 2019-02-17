import React, { Component } from "react";

import { Dropdown, Icon, abel, List, Table, Menu, Label } from 'semantic-ui-react'


class PinModesList extends Component {

  render() {
  	let { modes } = this.props;
  	if (!Array.isArray(modes) || !modes.length) 
  	 	return <Label />
  	 else {
  	 	let stateOptions = [];
  	 	let defaultValue;
  	 	modes.map(mode => {
  	 		stateOptions.push(
  	 		{ key: mode, value: mode, text: mode }
  	 		);
  	 		if (defaultValue == null) {
  	 		   defaultValue = mode;
  	 		}
  	 		
  	 	})
  	 	console.log('defaultValue: ' + defaultValue)
  		return (
    			<Dropdown simple item defaultValue={defaultValue} options={stateOptions} />
    	) 	
  	 }
  }
}

export default (PinModesList);
