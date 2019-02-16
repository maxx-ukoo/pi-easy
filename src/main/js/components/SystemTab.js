const React = require('react');
const ReactDOM = require('react-dom');
import { Label, List } from 'semantic-ui-react'

import axios from "axios";

class SystemTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = {sysinfo: {}};
	}
	
	componentDidMount() {
	    axios.get('/api/sysinfo')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.sysinfo = response.data;
                this.setState(newState);
            });
	}

	render() {
	    let { sysinfo } = this.state;
    
        return (
        <List divided selection>
	         <List.Item>
	      		<Label horizontal>
	        		Platform ID: 
	      		</Label>
	      		{sysinfo.platformID}
	    	</List.Item>
	    	<List.Item>
	      		<Label horizontal>
	        		Platform Name: 
	      		</Label>
	      		{sysinfo.platformName}
	    	</List.Item>
    	</List>
        )

	}
}

export default (SystemTab);