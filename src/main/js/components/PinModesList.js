import React, { Component } from "react";

import { Dropdown, Icon, abel, List, Table, Menu, Label } from 'semantic-ui-react'
import { find } from 'lodash';

class PinModesList extends Component {

  onChange = (e, data) => {
    let { pin } = this.props;
  	this.props.onPinModeChange(pin, data.value);
  }

  render() {
  	let { supportedPinModes } = this.props.pin;
  	
  	let defaultValue;
  	const config = find(this.props.pinConfig, { address: Number(this.props.pin.address) });
  	if (config) {
  		defaultValue = config.pinMode;
  	}  	
  	
  	if (!Array.isArray(supportedPinModes) || !supportedPinModes.length) 
  	 	return <Label />
  	 else {
  	 	let stateOptions = [];
  	 	
  	 	supportedPinModes.map(mode => {
  	 		stateOptions.push(
  	 		{ key: mode, value: mode, text: mode }
  	 		);
  	 	})
  		return (
    			<Dropdown simple item
    				value={defaultValue}
    				onChange={this.onChange} 
    				options={stateOptions} />
    	) 	
  	 }
  }
}

export default (PinModesList);
