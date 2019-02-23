const React = require('react');
const ReactDOM = require('react-dom');
import { Input, Icon, List, Table, Menu, Label } from 'semantic-ui-react'


import axios from "axios";

import PinRow from './PinRow'


class PinListTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = { config: {
							pins: [],
							config: []
						}
					};
	}
	
	componentDidMount() {
	    axios.get('/api/pins')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.config = response.data;
                this.setState(newState);
            });
	}
	
	onPinModeChange = (pin, newMode) => {


    	
    	const pinSettingsPayload = {
    	 	address: pin.address,
			name: pin.name,
    		pinMode: newMode,
    		pullResistance: 'OFF'
		};
		const url = '/api/pin/config';
		axios.put(url, JSON.stringify(pinSettingsPayload),{
        	headers: {
            	'Content-Type': 'application/json',
        	}
    	}).then(response => {
			console.log(response)
		});

  	}

	render() {
	    let { pins } = this.state.config;
	    let { config } = this.state.config;
	    
	    if (!Array.isArray(config) || !config.length) {
	    	config = [];
	    }
	    
	    console.log('--------------')
	    console.log(pins);
	    console.log(config);
	    
	    if (!Array.isArray(pins) || !pins.length) {
            return  <Label>Loading...</Label>
        } else {
            return (
             <Table celled>
			    <Table.Header>
			      <Table.Row>
			        <Table.HeaderCell>Name</Table.HeaderCell>
			        <Table.HeaderCell>Address</Table.HeaderCell>
			        <Table.HeaderCell>Mode</Table.HeaderCell>
			        <Table.HeaderCell>PullUP</Table.HeaderCell>
			      </Table.Row>
			    </Table.Header>

			    <Table.Body>
			     {pins.map(pin => (
			     	  <PinRow pin={pin} pinConfig={config} onPinModeChange={this.onPinModeChange} />
			      	))}  
			    </Table.Body>
 			 </Table>)
        }
	}
}

export default (PinListTab);