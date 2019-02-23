import React, { Component } from "react";

import { Dropdown, Table, Input } from 'semantic-ui-react';
import { Checkbox, Segment } from 'semantic-ui-react'

import { find } from 'lodash';
import axios from "axios";

class PinRow extends Component {

  onPinModeChange = (e, data) => {
    let { pin } = this.props;
  	this.props.onPinModeChange(pin, data.value);
  }
  
  onStateChange = (e, data) => {
    let { pin } = this.props;
  	console.log(data.checked);
  	let value;
  	if (data.checked) {
  		value = 'HIGH';
  	} else {
  	    value = 'LOW';
  	}
  	
  	
  	let cmd = 'GPIO,'+pin.address+','+value;
  	console.log(cmd); 
  	
	const url = '/control';
		axios.get(url, {
  			params: {
    			cmd: cmd
  			}
		})
		.then(response => {
			console.log(response)
		});
  }

  render() {
  	let { pin, pinConfig } = this.props;
  	
  	let pinModes = [];
  	pin.supportedPinModes.map(mode => {
  	 	pinModes.push({ key: mode, value: mode, text: mode });
  	})
  	
  	let defaultPinModeValue;
  	let config = find(this.props.pinConfig, { address: Number(this.props.pin.address) });
  	if (config) {
  		defaultPinModeValue = config.pinMode;
  	}
  	
  	let pullUpModes = [];
  	console.log('1: => ' + pin.supportedPinPullResistance)
  	if (pin.supportedPinPullResistance) {
	  	if (!Array.isArray(pin.supportedPinPullResistance) || !pin.supportedPinPullResistance.length) {
	  		console.log('2: => ' + pin.supportedPinPullResistance)
	  		pin.supportedPinPullResistance.map(mode => {
	  	 			pullUpModes.push({ key: mode, value: mode, text: mode });
	  		})
	  	}
	}
  	let defaultPullUpMode = pullUpModes[0];
  	
  	return (
	 	<Table.Row>
        		<Table.Cell><Input value={pin.name} /></Table.Cell>
        		<Table.Cell>{pin.address}</Table.Cell>
        		<Table.Cell>
        			<Dropdown selection
        				placeholder='Select mode'
    					value={defaultPinModeValue}
    					onChange={this.onPinModeChange} 
    					options={pinModes} />
    			</Table.Cell>
        		<Table.Cell>
        			<Dropdown selection
        				 placeholder='Select pullUP'
        				 value={defaultPullUpMode}
        				 options={pullUpModes} />
        		</Table.Cell>
        		<Table.Cell>
        		    <Segment compact>
				    	<Checkbox toggle onChange={this.onStateChange}/>
    				</Segment>
        		</Table.Cell>
      	</Table.Row>  	
  		)	
  	}
  
}

export default (PinRow);