import React, { Component } from "react";

import { Dropdown, Icon, abel, List, Table, Menu, Label } from 'semantic-ui-react'


class PinModesList extends Component {

  onChange = (e, data) => {
    let { pin } = this.props;
  	this.props.onPinModeChange(pin, data.value);
  }

  render() {
  	let { supportedPinModes } = this.props.pin;
  	if (!Array.isArray(supportedPinModes) || !supportedPinModes.length) 
  	 	return <Label />
  	 else {
  	 	let stateOptions = [];
  	 	let defaultValue;
  	 	supportedPinModes.map(mode => {
  	 		stateOptions.push(
  	 		{ key: mode, value: mode, text: mode }
  	 		);
  	 		if (defaultValue == null) {
  	 		   defaultValue = mode;
  	 		}
  	 	})
  		return (
    			<Dropdown simple item 
    				defaultValue={defaultValue}
    				onChange={this.onChange} 
    				options={stateOptions} />
    	) 	
  	 }
  }
}

export default (PinModesList);
